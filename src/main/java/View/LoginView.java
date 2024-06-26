package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class LoginView extends JFrame{

    JButton logInButton;
    JButton signUpButton;
    JTextField userNameTextField;
    JPasswordField passwordTextField;
    JLabel applicationNameLabel;
    JLabel userNameLabel;
    JLabel passwordLabel;
    ImageIcon logo;

    public LoginView(){

        this.setTitle("VeloCity");
        this.setResizable(false);
        this.setLayout(null);
        this.setSize(800,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(248,239,217));

        logo = new ImageIcon("Velocity.png");
        this.setIconImage(logo.getImage());


        applicationNameLabel = new JLabel("VELOCITY");
        applicationNameLabel.setForeground(new Color(255,81,0));
        applicationNameLabel.setFont(new Font("Franklin Gothic Heavy",Font.ITALIC, 50));
        applicationNameLabel.setBounds(275,60,800,60);

        logInButton = new JButton("Log In");
        logInButton.setBounds(275,350,100,40);
        logInButton.setBorderPainted(false);
        logInButton.setFocusPainted(false);
        logInButton.setBackground(new Color(255,81,0));
        logInButton.setForeground(new Color(248,239,217));

        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(400,350,100,40);
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setBackground(new Color(255,81,0));
        signUpButton.setForeground(new Color(248,239,217));

        userNameTextField = new JTextField();
        userNameTextField.setBounds(235, 150,300,50);
        userNameTextField.setFont(new Font("Metropolis",Font.PLAIN,20));

        passwordTextField = new JPasswordField();
        passwordTextField.setBounds(235, 250,300,50);
        passwordTextField.setFont(new Font("Metropolis",Font.PLAIN,20));

        userNameLabel = new JLabel("Username:");
        userNameLabel.setBounds(100,150,100,50);
        userNameLabel.setForeground(new Color(255,81,0));
        userNameLabel.setFont(new Font("Metropolis",Font.PLAIN, 20));

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100,250,100,50);
        passwordLabel.setForeground(new Color(255,81,0));
        passwordLabel.setFont(new Font("Metropolis",Font.PLAIN, 20));

        this.add(applicationNameLabel);
        this.add(signUpButton);
        this.add(logInButton);
        this.add(userNameTextField);
        this.add(passwordTextField);
        this.add(userNameLabel);
        this.add(passwordLabel);
        this.setVisible(true);


    }

    public JButton getLogInButton() {
        return logInButton;
    }

    public JButton getSignUpButton() {
        return signUpButton;
    }

    public JTextField getUserNameTextField() {
        return userNameTextField;
    }

    public JPasswordField getPasswordTextField() {
        return passwordTextField;
    }

}
