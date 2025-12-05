package org.ausiankou;

import org.ausiankou.util.ConsoleUtil;
import org.ausiankou.util.HibernateUtil;

public class App
{
    public static void main( String[] args ) {
        try {
            new ConsoleUtil.UserConsoleService().start();
        } catch (Exception ex) {
            System.err.println("Error; " + ex.getMessage());
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
