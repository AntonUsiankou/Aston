package org.ausiankou;

import org.ausiankou.service.HibernateService;
import org.ausiankou.service.UserConsoleService;

public class App
{
    public static void main( String[] args ) {
        try {
            new UserConsoleService().start();
        } catch (Exception ex) {
            System.err.println("Error; " + ex.getMessage());
        } finally {
            HibernateService.shutdown();
        }
    }
}
