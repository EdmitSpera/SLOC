public class EasyExcelTest {

//    @Test
//    public void simpleRead() {
//        String fileName = "D:\\idea\\java-project\\SLOC\\easyExcel.xlsx";
//        EasyExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
//            for (DemoData demoData : dataList) {
//                System.out.println("读取到一条数据:" + demoData);
//            }
//        })).sheet().doRead();
//    }
//
//    @Test
//    public void simpleWrite(){
//        String fileName = "D:\\idea\\java-project\\SLOC\\easyExcel.xlsx";
//        ArrayList<DemoData> data = new ArrayList<>();
//        data.add(new DemoData("曾琳","粿条","女","宣传部",21,"行政管理","1321@qq.com","1325788452"));
//        data.add(new DemoData("李兵","小李","男","技术部",22,"计算机科学","lming@school.com","13812345678"));
//        EasyExcel.write(fileName, DemoData.class)
//                .sheet("模板")
//                .doWrite(() -> {
//                    // 分页查询数据
//                    return data;
//                });
//    }
}
