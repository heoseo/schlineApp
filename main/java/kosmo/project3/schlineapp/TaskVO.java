package kosmo.project3.schlineapp;

public class TaskVO {

    private String exam_idx; 		//과제,시험의 관리번호(PK)
    private String subject_idx; 	//각 과목의 인덱스(FK)
    private String exam_name;	 //과제 이름
    private String exam_postdate; //작성 날짜
    private String exam_date;	//제출마감일
    private String exam_type;		//과제(1), 시험(2)
    private String exam_content; //본문
    private String exam_scoring; 	//과제, 시험에 대한 배점

    //과제 확인 여부
    private String check_flag;

    //정답 작성자 확인
    private String user_id;
    private String user_name;
    //종합과제함용 과목명 가져오기
    private String subject_name;

    public String getExam_idx() {
        return exam_idx;
    }

    public void setExam_idx(String exam_idx) {
        this.exam_idx = exam_idx;
    }

    public String getSubject_idx() {
        return subject_idx;
    }

    public void setSubject_idx(String subject_idx) {
        this.subject_idx = subject_idx;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public String getExam_postdate() {
        return exam_postdate;
    }

    public void setExam_postdate(String exam_postdate) {
        this.exam_postdate = exam_postdate;
    }

    public String getExam_date() {
        return exam_date;
    }

    public void setExam_date(String exam_date) {
        this.exam_date = exam_date;
    }

    public String getExam_type() {
        return exam_type;
    }

    public void setExam_type(String exam_type) {
        this.exam_type = exam_type;
    }

    public String getExam_content() {
        return exam_content;
    }

    public void setExam_content(String exam_content) {
        this.exam_content = exam_content;
    }

    public String getExam_scoring() {
        return exam_scoring;
    }

    public void setExam_scoring(String exam_scoring) {
        this.exam_scoring = exam_scoring;
    }

    public String getCheck_flag() {
        return check_flag;
    }

    public void setCheck_flag(String check_flag) {
        this.check_flag = check_flag;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
}
