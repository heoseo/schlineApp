package kosmo.project3.schlineapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {
    @SerializedName("lists")
    public List<Datum> lists = null;

    public List<Datum> getLists() {
        return lists;
    }

    public void setLists(List<Datum> lists) {
        this.lists = lists;
    }

    public class Datum{
        @SerializedName("video_idx")
        public String video_idx;
        @SerializedName("video_title")
        public String video_title;
        @SerializedName("video_end")
        public String video_end;
        @SerializedName("server_saved")
        public String server_saved;
        public String getVideo_idx() {
            return video_idx;
        }

        public void setVideo_idx(String video_idx) {
            this.video_idx = video_idx;
        }

        public String getVideo_title() {
            return video_title;
        }

        public void setVideo_title(String video_title) {
            this.video_title = video_title;
        }

        public String getVideo_end() {
            return video_end;
        }

        public void setVideo_end(String video_end) {
            this.video_end = video_end;
        }

        public String getServer_saved() {
            return server_saved;
        }

        public void setServer_saved(String server_saved) {
            this.server_saved = server_saved;
        }
    }


}
