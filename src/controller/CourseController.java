package controller;
import entities.Course;
import entities.FilePathResponse;
import entities.User;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import service.CourseService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    /*添加菜*/
    @RequestMapping("/addCourse")
    @ResponseBody
    public boolean addCourse(Course course, HttpSession session) {
        /*获取当前时间*/
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
        String currentDate = sdf.format(date);
        /*将当前时间设置到菜表*/
        course.setC_date(currentDate);
        /*获取第一张图片，将第一张设置course中*/
        String firstImgPath = (String) session.getAttribute("firstImgPath");
        course.setC_firstImage(firstImgPath);
        /*获取user对象*/
        User user = (User) session.getAttribute("user");
        User user1 = new User();
        user1.setU_id(user.getU_id());
        user1.setUsername(user.getUsername());
       /* 把user1添加到course中*/
        course.setUser(user1);
        boolean b=courseService.insertCourse(course);
        /*当前添加菜设置在session中*/
        session.setAttribute("currentShowCourse",course);
        return b;
    }

    /*上传图片*/
    @RequestMapping("/upload")
    @ResponseBody
    public FilePathResponse upload(MultipartFile file, HttpServletRequest request, HttpSession session) throws IOException {
        /*获取完整的路径名*/
        String path = request.getServletContext().getRealPath("/upload/") + file.getOriginalFilename();
        /*将上传文件保存到相应位置*/
        file.transferTo(new File(path));
        /*设置具体的位置*/
        file.transferTo(new File("E:/IntelliJ IDEA Project/SSM/web/upload/" + file.getOriginalFilename()));
        FilePathResponse path1 = new FilePathResponse();
        /*文件设置在path1中*/
        path1.setLink("/upload/" + file.getOriginalFilename());
       /* 获取第一张图片*/
        String firstImgPath = (String) session.getAttribute("firstImgPath");
        if (firstImgPath == null) {
            session.setAttribute("firstImgPath", "/upload/" + file.getOriginalFilename());
        }
        return path1;
    }
    /*通过菜的id修改点赞数*/
    @RequestMapping("updateCoursePraise/{c_id}")
    @ResponseBody
    public boolean updateCoursePraise(@PathVariable("c_id") Integer c_id){
        return  courseService.updateCoursePraise(c_id);
    }
    /*查询属于一种类型的菜*/
    @RequestMapping("/selectAllCourse")
    public String selectAllCourse(Course course,Model model) {
        model.addAttribute("selectAllCourse",courseService.selectAllCourse(course));
        return  "menuClassifies";
    }
/* 查询上传的菜
    @RequestMapping("selectCourse/{c_id}")
    public String selectCourse(@PathVariable("c_id") Integer c_id,Model model) {
        model.addAttribute("selectCourseName",courseService.selectCourse(c_id));
        return "selectCourse";
    }*/

}