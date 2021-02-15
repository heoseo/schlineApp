package kosmo.project3.schlineapp.team;

public class TeamVO {

    private String board_idx;
    private String subject_idx;
    private String board_type;
    private String user_id;
    private String board_title;
    private String board_content;
    private String board_file;
    private String board_postdate;
    private String board_flag_te;
    private String exam_idx;
    private String team_num;
    //다른 테이블과 조인
    private String user_name;
    private String subject_name;
    private String exam_name;
    private String exam_content;
    private String exam_date;

    public String getBoard_idx() {
        return board_idx;
    }

    public void setBoard_idx(String board_idx) {
        this.board_idx = board_idx;
    }

    public String getSubject_idx() {
        return subject_idx;
    }

    public void setSubject_idx(String subject_idx) {
        this.subject_idx = subject_idx;
    }

    public String getBoard_type() {
        return board_type;
    }

    public void setBoard_type(String board_type) {
        this.board_type = board_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBoard_title() {
        return board_title;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

    public String getBoard_content() {
        return board_content;
    }

    public void setBoard_content(String board_content) {
        this.board_content = board_content;
    }

    public String getBoard_file() {
        return board_file;
    }

    public void setBoard_file(String board_file) {
        this.board_file = board_file;
    }

    public String getBoard_postdate() {
        return board_postdate;
    }

    public void setBoard_postdate(String board_postdate) {
        this.board_postdate = board_postdate;
    }

    public String getBoard_flag_te() {
        return board_flag_te;
    }

    public void setBoard_flag_te(String board_flag_te) {
        this.board_flag_te = board_flag_te;
    }

    public String getExam_idx() {
        return exam_idx;
    }

    public void setExam_idx(String exam_idx) {
        this.exam_idx = exam_idx;
    }

    public String getTeam_num() {
        return team_num;
    }

    public void setTeam_num(String team_num) {
        this.team_num = team_num;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public String getExam_content() {
        return exam_content;
    }

    public void setExam_content(String exam_content) {
        this.exam_content = exam_content;
    }

    public String getExam_date() {
        return exam_date;
    }

    public void setExam_date(String exam_date) {
        this.exam_date = exam_date;
    }
}
