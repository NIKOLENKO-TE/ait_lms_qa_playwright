package lms_pages.UI;

public class UserCredentials {
    private final String user_email;
    private final String user_password;
    private static final String DEFAULT_PASSWORD = "LMS-dev-pass-2024";
    private static final String DOMAIN = "@dev-lms.de";
    public static final UserCredentials STUDENT_NOT_CONFIRMED_WITHOUT_EMAIL = new UserCredentials("", DEFAULT_PASSWORD);
    public static final UserCredentials STUDENT_NOT_CONFIRMED_WITHOUT_PASS = new UserCredentials("s12" + DOMAIN, null);
    public static final UserCredentials STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_4 = new UserCredentials("s14" + DOMAIN, null);
    public static final UserCredentials STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_NONE = new UserCredentials("s13" + DOMAIN, null);
    public static final UserCredentials STUDENT_CONFIRMED_PRIMARY_COHORT_NONE = new UserCredentials("s11" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials STUDENT_CONFIRMED_PRIMARY_COHORT_1 = new UserCredentials("s01" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_NONE = new UserCredentials("s09" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_5 = new UserCredentials("s05" + DOMAIN, DEFAULT_PASSWORD);

    public static final UserCredentials TEACHER_CONFIRMED_WITHOUT_ZOOM_ACCOUNT = new UserCredentials("t05" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT = new UserCredentials("t06" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD = new UserCredentials("t07" + DOMAIN, null);
    public static final UserCredentials TEACHER_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT = new UserCredentials("t02" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials TEACHER_CONFIRMED_WITH_VALID_ZOOM_ACCOUNT = new UserCredentials("t04" + DOMAIN, DEFAULT_PASSWORD);  // ! andre.reutow@gmail.com
    public static final UserCredentials TEACHER_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT = new UserCredentials("t03" + DOMAIN, DEFAULT_PASSWORD);

    public static final UserCredentials ADMIN_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT = new UserCredentials("a02" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD = new UserCredentials("a03" + DOMAIN, null);
    public static final UserCredentials ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT = new UserCredentials("a01" + DOMAIN, DEFAULT_PASSWORD);
    public static final UserCredentials ADMIN_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT = new UserCredentials("a05" + DOMAIN, DEFAULT_PASSWORD);

    public static final UserCredentials NON_EXISTENT_ACCOUNT = new UserCredentials("sss" + DOMAIN, "ssSSSs@2012");

    public UserCredentials(String username, String password) {
        this.user_email = username;
        this.user_password = password;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_password() {
        return user_password;
    }
}