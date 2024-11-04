import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public  class DemoData { // 将此类声明为 static
    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 户外ID
     */
    private String nickname;

    /**
     * 性别
     */
    private String gender;

    /**
     * 部门
     */
    private String department;

    /**
     * 年级
     */
    private int grade;

    /**
     * 专业
     */
    private String major;

    /**
     * 校内邮箱
     */
    private String mail;

    /**
     * 电话
     */
    private String phone;
}