package main.java;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Student {
    int roll, attendance, marks;
    String name;

    Student(int r, String n, int a, int m) {
        roll = r; name = n; attendance = a; marks = m;
    }

    public String toString() {
        return roll + "," + name + "," + attendance + "," + marks;
    }
}

public class Main extends JFrame {

    ArrayList<Student> list = new ArrayList<>();
    HashMap<Integer, Student> map = new HashMap<>();
    Stack<String> undo = new Stack<>();
    Queue<String> reports = new LinkedList<>();

    File file = new File("students.txt");

    CardLayout card = new CardLayout();
    JPanel main = new JPanel(card);

    JTextField tRoll, tName, tAtt, tMarks;
    JTextArea area;

    public Main() {
        setTitle("Student Attendance & Performance Tracker");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        loadFile();

        main.add(rolePanel(), "role");
        main.add(teacherLogin(), "tlogin");
        main.add(studentLogin(), "slogin");
        main.add(teacherPanel(), "teacher");
        main.add(studentPanel(), "student");

        add(main);
        card.show(main, "role");
    }

    // ================= ROLE =================
    JPanel rolePanel() {
        JPanel p = bgPanel();
        p.setLayout(null);

        JButton t = new JButton("TEACHER LOGIN");
        JButton s = new JButton("STUDENT LOGIN");

        t.setBounds(350, 220, 200, 40);
        s.setBounds(350, 280, 200, 40);

        p.add(title());
        p.add(t); p.add(s);

        t.addActionListener(e -> card.show(main, "tlogin"));
        s.addActionListener(e -> card.show(main, "slogin"));

        return p;
    }

    // ================= TEACHER LOGIN =================
    JPanel teacherLogin() {
        JPanel p = bgPanel();
        p.setLayout(null);

        JTextField u = new JTextField();
        JPasswordField pw = new JPasswordField();

        u.setBounds(350, 220, 200, 30);
        pw.setBounds(350, 260, 200, 30);

        JButton login = new JButton("LOGIN");
        login.setBounds(380, 310, 140, 35);

        p.add(title());
        p.add(new JLabel("ID")).setBounds(300,220,50,30);
        p.add(new JLabel("Password")).setBounds(270,260,80,30);
        p.add(u); p.add(pw); p.add(login);

        login.addActionListener(e -> {
            if (u.getText().equals("teacher") &&
                new String(pw.getPassword()).equals("teacher123"))
                card.show(main, "teacher");
            else
                JOptionPane.showMessageDialog(this,"Invalid Login");
        });
        return p;
    }

    // ================= STUDENT LOGIN =================
    JPanel studentLogin() {
        JPanel p = bgPanel();
        p.setLayout(null);

        JTextField roll = new JTextField();
        roll.setBounds(350, 240, 200, 30);

        JButton login = new JButton("LOGIN");
        login.setBounds(380, 290, 140, 35);

        p.add(title());
        p.add(new JLabel("Roll No")).setBounds(280,240,80,30);
        p.add(roll); p.add(login);

        login.addActionListener(e -> {
            int r = Integer.parseInt(roll.getText());
            if (map.containsKey(r)) {
                studentView(r);
                card.show(main, "student");
            } else
                JOptionPane.showMessageDialog(this,"Record Not Found");
        });
        return p;
    }

    // ================= TEACHER PANEL =================
    JPanel teacherPanel() {
        JPanel p = bgPanel();
        p.setLayout(new BorderLayout());

        JPanel left = new JPanel(new GridLayout(4,2));
        left.setOpaque(false);

        tRoll = new JTextField();
        tName = new JTextField();
        tAtt = new JTextField();
        tMarks = new JTextField();

        left.add(new JLabel("Roll")); left.add(tRoll);
        left.add(new JLabel("Name")); left.add(tName);
        left.add(new JLabel("Attendance")); left.add(tAtt);
        left.add(new JLabel("Marks")); left.add(tMarks);

        JPanel btn = new JPanel(new GridLayout(2,3));
        btn.setOpaque(false);

        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton report = new JButton("Report");
        JButton save = new JButton("Save");

        btn.add(add); btn.add(update); btn.add(report); btn.add(save);

        area = new JTextArea();
        JScrollPane sp = new JScrollPane(area);

        p.add(title(), BorderLayout.NORTH);
        p.add(left, BorderLayout.WEST);
        p.add(btn, BorderLayout.CENTER);
        p.add(sp, BorderLayout.EAST);

        add.addActionListener(e -> {
            Student s = new Student(
                Integer.parseInt(tRoll.getText()),
                tName.getText(),
                Integer.parseInt(tAtt.getText()),
                Integer.parseInt(tMarks.getText())
            );
            list.add(s); map.put(s.roll,s);
            area.append("Added: "+s.name+"\n");
        });

        update.addActionListener(e -> {
            int r = Integer.parseInt(tRoll.getText());
            Student s = map.get(r);
            s.attendance = Integer.parseInt(tAtt.getText());
            s.marks = Integer.parseInt(tMarks.getText());
            area.append("Updated\n");
        });

        report.addActionListener(e -> {
            reports.add("Attendance Report");
            reports.add("Performance Report");
            while(!reports.isEmpty())
                area.append(reports.poll()+" Generated\n");
        });

        save.addActionListener(e -> saveFile());

        return p;
    }

    // ================= STUDENT PANEL =================
    JPanel studentPanel() {
        JPanel p = bgPanel();
        p.setLayout(new BorderLayout());
        p.add(title(), BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    void studentView(int r) {
        Student s = map.get(r);
        area.setText(
            "Roll: "+s.roll+"\n"+
            "Name: "+s.name+"\n"+
            "Attendance: "+s.attendance+"%\n"+
            "Marks: "+s.marks+"\n"
        );
    }

    // ================= FILE =================
    void saveFile() {
        try(PrintWriter pw = new PrintWriter(file)) {
            for(Student s:list) pw.println(s);
        } catch(Exception e){}
    }

    void loadFile() {
        if(!file.exists()) return;
        try(Scanner sc=new Scanner(file)) {
            while(sc.hasNextLine()) {
                String[] d=sc.nextLine().split(",");
                Student s=new Student(
                    Integer.parseInt(d[0]),d[1],
                    Integer.parseInt(d[2]),Integer.parseInt(d[3])
                );
                list.add(s); map.put(s.roll,s);
            }
        } catch(Exception e){}
    }

    // ================= UI HELPERS =================
    JLabel title() {
        JLabel l = new JLabel(
            "STUDENT ATTENDANCE & PERFORMANCE TRACKER SYSTEM",
            JLabel.CENTER);
        l.setFont(new Font("Arial",Font.BOLD,18));
        l.setForeground(Color.WHITE);
        return l;
    }

    JPanel bgPanel() {
        return new JPanel() {
            Image bg = new ImageIcon("bg.jpg").getImage();
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg,0,0,getWidth(),getHeight(),this);
            }
        };
    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }
}
