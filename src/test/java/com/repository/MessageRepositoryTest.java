package com.repository;

import com.model.Message;
import com.model.User;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageRepositoryTest {
    private MessageRepository mRepository = new MessageRepository();
    private UserRepository uRepository = new UserRepository();

    @After
    public void after() {
        for (Message m : mRepository.findAllMessages()) {
            mRepository.deleteMessage(m);
        }
        for (User u : uRepository.findAllUsers()) {
            uRepository.deleteUser(u);
        }
    }

    @Test
    public void saveMessage() throws Exception {
        Message msg = getMessage();

        mRepository.saveMessage(msg);

        assertThat(mRepository.findAllMessages().size() == 1).isTrue();
    }

    @Test
    public void updateMessage() {
        Message msg = getMessage();
        mRepository.saveMessage(msg);
        String newText = "new" + msg.getText();

        msg.setText(newText);
        mRepository.saveMessage(msg);

        assertThat(mRepository.findAllMessages().size() == 1).isTrue();
        assertThat(mRepository.findById(msg.getId()).getText()).isEqualTo(newText);
    }

    @Test
    public void updateMessageNoCascadeUpdateUser() {
        Message msg = getMessage();
        mRepository.saveMessage(msg);
        String oldName = msg.getUser().getName();
        String newName = "new" + oldName;
        msg.getUser().setName(newName);

        mRepository.saveMessage(msg);

        assertThat(mRepository.findAllMessages().size() == 1).isTrue();
        assertThat(mRepository.findById(msg.getId()).getUser().getName()).isEqualTo(oldName);
        assertThat(mRepository.findById(msg.getId()).getUser().getName()).isNotEqualTo(newName);
    }

    @Test
    public void deleteMessage() throws Exception {
        Message msg = getMessage();
        Message msg2 = getMessage();
        mRepository.saveMessage(msg);

        mRepository.deleteMessage(msg);

        assertThat(mRepository.findAllMessages().size() == 0).isTrue();
        assertThat(mRepository.deleteMessage(null)).isFalse();
        assertThat(mRepository.deleteMessage(msg2)).isFalse();
    }

    @Test
    public void findAllMessages() throws Exception {
        mRepository.saveMessage(getMessage("Some text"));
        mRepository.saveMessage(getMessage("Some text"));
        mRepository.saveMessage(getMessage("Some text"));

        assertThat(mRepository.findAllMessages().size() == 3).isTrue();
    }

    @Test
    public void findAllMessagesByUser() throws Exception {
        Message msg = getMessage("Some text");
        Message msg2 = getMessage(msg.getUser(), "Some super text");
        Message msg3 = getMessage(msg.getUser(), "Some extra text");
        User user = new UserRepositoryTest().getUser("new" + msg.getUser().getEmail());
        Message msg4 = getMessage(user, "Some mega text");
        mRepository.saveMessage(msg);
        mRepository.saveMessage(msg2);
        mRepository.saveMessage(msg3);
        mRepository.saveMessage(msg4);

        assertThat(mRepository.findAllMessages(msg.getUser()).size() == 3).isTrue();
        assertThat(mRepository.findAllMessages(user).size() == 1).isTrue();
        assertThat(mRepository.findAllMessages().size() == 4).isTrue();
        assertThat(mRepository.findAllMessages(null).size() == 0).isTrue();
    }

    @Test
    public void findById() throws Exception {
        Message msg = getMessage();
        mRepository.saveMessage(msg);

        assertThat(mRepository.findById(msg.getId())).isEqualTo(msg);
        assertThat(mRepository.findById(0)).isNull();
        assertThat(mRepository.findById(-1)).isNull();
    }

    @Test
    public void findByText() throws Exception {
        String text = "Super-mega text";
        String text2 = "Super-mega text2";

        Message msg = getMessage(text);
        mRepository.saveMessage(msg);

        User user = new UserRepositoryTest().getUser("new" + msg.getUser().getEmail());
        Message msg2 = getMessage(user, text2);
        mRepository.saveMessage(msg2);
        msg2.setId(0);
        mRepository.saveMessage(msg2);

        assertThat(mRepository.findByText(text).size() == 1).isTrue();
        assertThat(mRepository.findByText(text).contains(msg)).isTrue();
        assertThat(mRepository.findByText(text2).size() == 2).isTrue();
        assertThat(mRepository.findByText(text2).contains(msg2)).isTrue();
        assertThat(mRepository.findByText(null).size() == 0).isTrue();
    }

    public Message getMessage() {
        User user = new UserRepositoryTest().getUser();
        uRepository.saveUser(user);
        return new Message(user, "some super message");
    }

    public Message getMessage(String text) {
        Message message = getMessage();
        message.setText(text);
        return message;
    }

    public Message getMessage(User user) {
        uRepository.saveUser(user);
        return new Message(user, "some super message");
    }

    public Message getMessage(User user, String text) {
        Message msg = getMessage(user);
        msg.setText(text);
        return msg;
    }
}