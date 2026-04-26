/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package methods;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.kalc.pos.sales.JPanelTicket;

/**
 *
 * @author John
 */
public class ClassMethodShow {

    private static List<String> methodsList;
    private static List<String> variablesList;
    private static StringBuilder sb;
    private static StringBuilder fieldString;

    public static void main(String args[]) throws IOException {

        methodsList = new ArrayList<>();
        variablesList = new ArrayList<>();

        Class tClass = JPanelTicket.class;

        Field[] fields = tClass.getDeclaredFields();
        Method[] methods = tClass.getDeclaredMethods();

        for (int i = 0; i < fields.length; i++) {
            fieldString = new StringBuilder("");
            if ((fields[i].getModifiers() & 1) == 1) {
                fieldString.append("public ");
            }
            if ((fields[i].getModifiers() & 2) == 2) {
                fieldString.append("private ");
            }
            if ((fields[i].getModifiers() & 4) == 4) {
                fieldString.append("protected ");
            }
            if ((fields[i].getModifiers() & 8) == 8) {
                fieldString.append("static ");
            }
            if ((fields[i].getModifiers() & 16) == 16) {
                fieldString.append("final ");
            }

            int last = fields[i].getType().toString().lastIndexOf('.');
            fieldString.append((last == -1) ? fields[i].getType() : fields[i].getType().toString().substring(last + 1));
            fieldString.append(" ");
            fieldString.append(fields[i].getName());

            variablesList.add(fieldString.toString());

        }

        for (int i = 0; i < methods.length; i++) {
            sb = new StringBuilder("");
            if ((methods[i].getModifiers() & 1) == 1) {
                sb.append("public ");
            }
            if ((methods[i].getModifiers() & 2) == 2) {
                sb.append("private ");
            }
            if ((methods[i].getModifiers() & 4) == 4) {
                sb.append("protected ");
            }
            if ((methods[i].getModifiers() & 8) == 8) {
                sb.append("static ");
            }
            if ((methods[i].getModifiers() & 16) == 16) {
                sb.append("final ");
            }

            int last = methods[i].getReturnType().toString().lastIndexOf('.');
            sb.append((last == -1) ? methods[i].getReturnType() : methods[i].getReturnType().toString().substring(last + 1));
            sb.append(" ");
            sb.append(methods[i].getName());
            sb.append("(");

            if (methods[i].getParameterTypes().length != 0) {
                Class[] params = methods[i].getParameterTypes();

                for (Class p : params) {
                    String cn = p.getName();
                    last = cn.lastIndexOf('.');
                    sb.append((last == -1) ? cn : cn.substring(last + 1));
                    sb.append(", ");
                }
                sb.setLength(sb.length() - 2);
            }
            sb.append(")");

            methodsList.add(sb.toString());

        }

        Collections.sort(variablesList);
        for (String str : variablesList) {
            System.out.println(str);
        }

        System.out.println("");

        Collections.sort(methodsList);
        for (String str : methodsList) {
            System.out.println(str);
        }

        //     Field[] fields = tClass.getFields(); // returns inherited members but not private members.
        //  Field[] fields = LoyaltyCard.class.getDeclaredFields();
        //   Field[] fields = tClass.getDeclaredFields();
//        List<Field> fieldList = Arrays.asList(fields).stream().filter(field -> Modifier.isPublic(field.getModifiers())).collect(
//                Collectors.toList());
//        for (Field f : fields) {
//            System.out.println(f);
//        }
    }

}
