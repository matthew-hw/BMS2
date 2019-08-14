import com.mysql.jdbc.Connection;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class BMS extends HttpServlet {
    private static final long serialVersionUID = 1L;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/BMS";
    static final String USER = "root";
    static final String PASS = "QWEASD";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BMS() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
        System.out.printf("System Starting");
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = null;
        Statement stmt = null;
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>\n" + "<html>\n" +
                "<head><title>" + "图书管理系统" + "</title></head>\n" +
                "<body bgcolor=\"#f0f0f0\">\n" + "<h1>图书管理系统</h1> ");
        try {
            // 注册 JDBC 驱动器
            Class.forName("com.mysql.jdbc.Driver");

            // 打开一个连接
            conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行 SQL 查询
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name, available,mark,last_modify_time FROM Books";
            ResultSet rs = stmt.executeQuery(sql);
            out.print("<table border=\"1\"\n>" +
                    "<tr><td>ID</td><td>书名</td><td>状态</td><td>操作</td>" +
                    "</tr></table>");
            while (rs.next()) {
                // 通过字段检索
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean available = rs.getBoolean("available");
                int mark = rs.getInt("mark");
                Date date = rs.getDate("last_modify_time");
                // 输出数据
                out.println("<table border=\"1\">\n" +
                        "<tr>\n" +
                        "<td>" + id + "</td>\n" +
                        "<td>" + name + "</td>\n");
                if (available) {
                    out.print("<td>可借</td>" +
                            "<td><form action=\"/BMS/submit.do\" method = \"POST\">\n" +
                            "<input type=\"hidden\" name=\"ID\" value=\"" + id + "\">" +
                            "<input type=\"hidden\" name=\"method\" value=\"borrow\">" +
                            "<input type=\"text\" name=\"mark\">\n" +
                            "<input type=\"submit\" value=\"借阅\">\n" +
                            "</form> </td>");
                } else out.print("<td>借出</td>" +
                        "<td>" + mark + "</td>" +
                        "<td>" + date.toString() + "</td>" +
                        "<td><form action=\"/BMS/submit.do\" method = \"POST\">\n" +
                        "<input type=\"hidden\" name=\"ID\" value=\"" + id + "\">" +
                        "<input type=\"hidden\" name=\"method\" value=\"return\">" +
                        "<input type=\"hidden\" name=\"mark\" value=\"0\">\n" +
                        "<input type=\"submit\" value=\"归还\">\n" +
                        "</form> </td>");
                out.println("</tr>\n" + "</table>");
            }
            out.println("</body></html>");
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 最后是用于关闭资源的块
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Connection conn = null;
        Statement stmt =null;
//        PreparedStatement pstmt = null;
        boolean method = request.getParameter("method").equals("return");
        int ID = Integer.parseInt(request.getParameter("ID"));
        Date date = new Date(System.currentTimeMillis());
        String mark = request.getParameter("mark");
        try {
            // 注册 JDBC 驱动器
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
//            String sql = "UPDATE books SET available = ? , mark = ? , last_modify_time = ? WHRER ID = ?";
//            pstmt = conn.prepareStatement(sql );
//            pstmt.setInt(4,ID);
//            if(!method){
//                pstmt.setBoolean(1,false);
//                pstmt.setInt(2, Integer.parseInt(request.getParameter("mark")));
//                pstmt.setDate(3,date);
//            }else{
//                pstmt.setBoolean(1, true);
//                pstmt.setInt(2,0);
//                pstmt.setDate(3, date);
//            }
//            pstmt.executeUpdate();
//            pstmt.close();
            String sql = "UPDATE books SET available = " +
                    String.valueOf(method) + " ,mark = " + mark +
                    " WHERE ID = " + ID;
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 最后是用于关闭资源的块
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        doGet(request, response);
    }
}