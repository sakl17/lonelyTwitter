package ca.ualberta.cs.lonelytwitter;

import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Created by romansky on 1/12/16.
 */
public abstract class Tweet implements Tweetable {
    @JestId
    protected String id;
    protected Date date;
    protected String message;

    public Tweet(Date date, String message) throws TweetTooLongException {
        setDate(date);
        setMessage(message);
    }

    public Tweet(String message) throws TweetTooLongException {
        setMessage(message);
        this.date = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract Boolean isImportant();

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) throws TweetTooLongException {
        if (message.length() > 140) {
            throw new TweetTooLongException();
        }
        this.message = message;
    }

    @Override
    public String toString() {
        return date.toString() + " | " + message;
    }
}
