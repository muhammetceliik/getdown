//
// Getdown - application installer, patcher and launcher
// Copyright (C) 2004-2018 Getdown authors
// https://github.com/threerings/getdown/blob/master/LICENSE

package com.threerings.getdown.launcher;

import com.samskivert.swing.GroupLayout;
import com.samskivert.swing.HGroupLayout;
import com.samskivert.swing.VGroupLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

public final class AuthenticationPanel extends JDialog implements ActionListener {
    private final Getdown _getdown;
    private final JTextField username;
    private final JTextField password;

    public AuthenticationPanel(Getdown getdown) {
        super(null, ModalityType.APPLICATION_MODAL);
        _getdown = getdown;

        setLayout(new VGroupLayout());
        setResizable(false);
        setTitle("Kullanıcı Giriş Paneli");

        JPanel box1 = new JPanel(new HGroupLayout());
        JLabel usernameLabel = new JLabel("Kullanıcı Adı");
        box1.add(usernameLabel);
        username = new JTextField();
        username.setPreferredSize(new Dimension(100, 20));
        box1.add(username);

        JPanel box2 = new JPanel(new HGroupLayout());
        JLabel passwordLabel = new JLabel("Şifre");
        box2.add(passwordLabel);
        password = new JTextField();
        password.setPreferredSize(new Dimension(100, 20));
        box2.add(password);

        add(box1);
        add(box2);

        JPanel row = GroupLayout.makeButtonBox(GroupLayout.CENTER);
        JButton button = new JButton("ok");
        button.setActionCommand("ok");
        button.addActionListener(this);
        row.add(button);
        getRootPane().setDefaultButton(button);
        add(row);
    }

    public void actionPerformed(ActionEvent e) {
        if (username.getText() != null && !username.getText().trim().isEmpty() && password.getText() != null && !password.getText().trim().isEmpty()) {
            _getdown._app.createUsernamePassword(username.getText(), password.getText());
            try {
                URLConnection urlConnection = _getdown._app.getRemoteURL("getdown.txt").openConnection();
                urlConnection.setRequestProperty("Authorization", _getdown._app.getBasicAuth());
                if (_getdown._app.conn.checkConnectStatus(urlConnection) != HttpURLConnection.HTTP_OK) {
                    showMessage("Hatalı kullanıcı adı veya şifre girdiniz.", "Hata");
                } else {
                    _getdown._app.writeAuthentication();
                    setVisible(false);
                }
            } catch (IOException ex) {
                showMessage("Bir hata oluştu.", "Hata");
            }
        } else {
            showMessage("Kullanıcı Adı ve Şifre alanları boş bırakılamaz.", "Hata");
        }
    }

    public void showMessage(final String msg, final String title)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JTextPane text = new JTextPane();
                text.setEditable(false);
                text.setText(msg);
                text.setPreferredSize(new Dimension(300, 100));
                JScrollPane scrollPane = new JScrollPane(text);
                JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 200);
    }
}
