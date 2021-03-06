package org.wdd.app.android.interestcollection.ui.images.model;

import java.util.List;

/**
 * Created by richard on 2/24/17.
 */

public class ImageDetail {

    public String title;
    public String time;
    public String tag;
    public String commentCount;
    public String imgUrl;
    public String summary;
    public List<Node> nodes;
    public String source;

    public static class Node {

        public boolean isImg;
        public String data;

        public Node(boolean isImg, String data) {
            this.isImg = isImg;
            this.data = data;
        }
    }

}
