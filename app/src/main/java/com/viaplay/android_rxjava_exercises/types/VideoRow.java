package com.viaplay.android_rxjava_exercises.types;

public class VideoRow {

    public int listId;
    public int videoId;
    public String name;

    public VideoRow(int listId, int videoId, String name) {
        this.listId = listId;
        this.videoId = videoId;
        this.name = name;
    }
}