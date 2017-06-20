package org.rajmoh.radio.pojo;

/**
 * Created by NEERAJ on 5/28/2017.
 */

public class ChannelInfo
{
private String channel_id;

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_image() {
        return channel_image;
    }

    public void setChannel_image(String channel_image) {
        this.channel_image = channel_image;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_url() {
        return channel_url;
    }

    public void setChannel_url(String channel_url) {
        this.channel_url = channel_url;
    }

    private String channel_image;
private String channel_name;
private String channel_url;
}
