/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package bisnisku.app;

/**
 *
 * @author Mahesa
 */
public class BisniskuApp {

    private static boolean checkSession() {
        int userId = UserSession.getUserId();

        if (userId <= 0) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
         java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (checkSession() == true) {
                    new MainTycoonSandbox().setVisible(true);
                } else {
                    new LoginForm().setVisible(true);
                }
            }
        });
    }
    
}
