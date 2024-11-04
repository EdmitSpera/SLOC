import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.github.javafaker.Faker;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Random;

public class FakerExcelTest {
    @Test
    public void excelWrite() {
        Collection<DemoData> data = createData(200);
        String fileName = "D:\\idea\\java-project\\SLOC\\easyExcel.xlsx";
        EasyExcel.write(fileName, DemoData.class)
                .sheet("test")
                .doWrite(() -> {
                    // 分页查询数据
                    return data;
                });
    }

    @Test
    public void simpleRead() {
        String fileName = "D:\\idea\\java-project\\SLOC\\easyExcel.xlsx";
        EasyExcel.read(fileName, ExcelDTO.class, new PageReadListener<ExcelDTO>(dataList -> {
            for (ExcelDTO demoData : dataList) {
                System.out.println("读取到一条数据:" + demoData);
            }
        })).sheet().doRead();
    }

    private static final Random random = new Random();
    private static final int MIN_VALUE = 100000;
    private static final int MAX_VALUE = 999999;

    public String generateSixDigitNumber() {
        return String.valueOf(random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE);
    }

    @Test
    public void test(){
        for (int i = 0; i < 10; i++) {
            System.out.println(generateSixDigitNumber());
        }
    }

    public Collection<DemoData> createData(int count) {
        Faker faker = new Faker(new Locale("zh-CN"));
        Collection<DemoData> demoDataList = new ArrayList<>();
        Random random = new Random();
        String[] departmentEnum = {"装备部", "训练部", "公关部", "宣传部", "秘书部"};
        for (int i = 0; i < count; i++) {
            String realName = faker.name().fullName();
            String nickName = faker.funnyName().name();
            String gender = (random.nextInt(2) == 0) ? "男" : "女";
            String department = departmentEnum[random.nextInt(5)];
            int grade = random.nextInt(15) + 10; // 生成 10 到 24 之间的年级
            String studentNumber = "20" + grade + generateSixDigitNumber();
            String major = faker.educator().course();
            String mailTemp = nickName.replaceAll("\\s+", "").toLowerCase();
            String mail = grade + mailTemp + "@stu.edu.cn";
            String phone = faker.phoneNumber().cellPhone();

            // 创建 DemoData 实例并添加到集合
            demoDataList.add(new DemoData(studentNumber,realName, nickName, gender, department, grade, major, mail, phone));
        }
        return demoDataList;
    }

    @Data
    public static class ExcelDTO {
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
        private String grade;

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
}
