package com.pay10.pg.core.ingenico.util;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validation
{
    private static int len;
    private static int i;
    private static char ch;
    private static boolean valid;
    
    static {
        Validation.len = 0;
        Validation.i = 0;
        Validation.valid = true;
    }
    
    public Validation() {
        Validation.valid = true;
    }
    
    public static void main(final String[] args) {
        final Validation o = new Validation();
    }
    
    public static boolean isUrl(final String strPanNo) {
        final String lRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        final Pattern p = Pattern.compile(lRegex);
        final Matcher m = p.matcher(strPanNo);
        return Validation.valid = m.matches();
    }
    
    public static boolean isMobileNO(final String strPanNo) {
        final Pattern p = Pattern.compile("[0-9]{10}");
        final Matcher m = p.matcher(strPanNo);
        return Validation.valid = m.matches();
    }
    
    public static boolean checkLength(final String input, final int length) {
        final boolean stat = false;
        return !isEmpty(input) || (length > 0 && input.length() <= length) || stat;
    }
    
    public static boolean isAmount(final String input) {
        boolean stat = false;
        try {
            final double d = Double.parseDouble(input);
            stat = true;
        }
        catch (Exception e) {
            stat = false;
        }
        return stat;
    }
    
    public static boolean isEmpty(String input) {
        input = input.trim();
        Validation.valid = true;
        return input != null && input.length() >= 1;
    }
    
    public static boolean validatePanNo(final String strPanNo) {
        final Pattern p = Pattern.compile("[a-z A-Z]{5}?[0-9]{4}?[a-z A-Z]{1}?");
        final Matcher m = p.matcher(strPanNo);
        return Validation.valid = m.matches();
    }
    
    public static boolean validateUserDates(final String strFromDate, final String strToDate) {
        boolean flag = true;
        if (isEmpty(strFromDate) && isEmpty(strToDate)) {
            final SimpleDateFormat sdfObj = new SimpleDateFormat("dd-MM-yyyy");
            try {
                final Date objFromDate = sdfObj.parse(strFromDate);
                final Date objToDate = sdfObj.parse(strToDate);
                if (!objFromDate.before(objToDate) && !objFromDate.equals(objToDate)) {
                    flag = false;
                }
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        return flag;
    }
    
    public static boolean onlyChar(final String input) {
        final Pattern p = Pattern.compile("[a-z A-Z]+");
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean onlyCharIntDot(final String input) {
        final Pattern p = Pattern.compile("[a-z A-Z 0-9 //.]+");
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean onlyCharInt(String input) {
        input = input.trim();
        final Pattern p = Pattern.compile("[a-z A-Z 0-9 ]+");
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean onlyInteger(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if (Validation.valid && (Validation.ch > '9' || Validation.ch < '0')) {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean emailFormat(String input) {
        input = input.trim();
        final Pattern p = Pattern.compile("[a-z A-Z]+\\.?+\\w*@{1}[a-z A-Z]+\\.{1}+[a-z A-Z]+\\.{0,1}[a-z A-Z]*");
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean onlyFloat(String input) {
        input = input.trim();
        int dotcount = 0;
        Validation.valid = true;
        String prefix = "";
        String postfix = "";
        int prefixvalue = 0;
        int postfixvalue = 0;
        try {
            Validation.len = input.length();
            if (input != null && Validation.len >= 1) {
                dotcount = 0;
                Validation.i = 0;
                Block_7: {
                    while (Validation.i < Validation.len) {
                        if (!Validation.valid) {
                            break;
                        }
                        Validation.ch = input.charAt(Validation.i);
                        if (Validation.valid && (Validation.ch > '9' || Validation.ch < '0') && Validation.ch != '.') {
                            break Block_7;
                        }
                        if (Validation.ch == '.') {
                            if (Validation.i == 0) {
                                Validation.valid = false;
                            }
                            else if (++dotcount == 1) {
                                prefix = input.substring(0, Validation.i);
                                postfix = input.substring(Validation.i + 1, input.length());
                                prefixvalue = Integer.parseInt(prefix);
                                postfixvalue = Integer.parseInt(postfix);
                                if (prefixvalue < 1 && postfixvalue < 1) {
                                    Validation.valid = false;
                                }
                            }
                        }
                        if (dotcount > 1) {
                            Validation.valid = false;
                        }
                        ++Validation.i;
                    }
                    return Validation.valid;
                }
                Validation.valid = false;
            }
            else {
                Validation.valid = false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyCharInteger(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != ' ') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyAlphaNumericWithSpecialChar(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != ' ' && Validation.ch != '!' && Validation.ch != '@' && Validation.ch != '#' && Validation.ch != '$' && Validation.ch != '%' && Validation.ch != '^' && Validation.ch != '&' && Validation.ch != '*' && Validation.ch != '(' && Validation.ch != ')' && Validation.ch != '-' && Validation.ch != '+' && Validation.ch != ',' && Validation.ch != '_') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyAlphaNumericWithSpecialCharacters(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != '_' && Validation.ch != '!' && Validation.ch != '@' && Validation.ch != '#' && Validation.ch != '$' && Validation.ch != '%' && Validation.ch != '^' && Validation.ch != '&' && Validation.ch != '*' && Validation.ch != '(' && Validation.ch != ')' && Validation.ch != '-' && Validation.ch != '+' && Validation.ch != '.' && Validation.ch != ' ' && Validation.ch != '\'') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyAlphaNumWithSpecialCharsLoginId(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != '@' && Validation.ch != '#' && Validation.ch != '$' && Validation.ch != '^' && Validation.ch != '(' && Validation.ch != ')' && Validation.ch != '.' && Validation.ch != '_') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyAlphaNumericWithSpecialChar1(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if (Validation.ch == ' ' || Validation.ch == '=' || Validation.ch == '@' || Validation.ch == '#' || Validation.ch == '$' || Validation.ch == '%' || Validation.ch == '^' || Validation.ch == '&' || Validation.ch == '*' || Validation.ch == '(' || Validation.ch == ')' || Validation.ch == '+' || Validation.ch == '\\') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean validDate(final String dt, final int grace) {
        boolean valid = true;
        final String fdt = "dd-MM-yyyy";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(fdt);
            sdf.setLenient(false);
            sdf.parse(dt);
        }
        catch (ParseException e) {
            valid = false;
        }
        catch (IllegalArgumentException e2) {
            valid = false;
        }
        return valid;
    }
    
    public static boolean onlyFloatArea(String input) {
        input = input.trim();
        int dotcount = 0;
        Validation.valid = true;
        final String prefix = "";
        final int prefixvalue = 0;
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            dotcount = 0;
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                if (!Validation.valid) {
                    break;
                }
                Validation.ch = input.charAt(Validation.i);
                if (Validation.valid && (Validation.ch > '9' || Validation.ch < '0') && Validation.ch != '.') {
                    Validation.valid = false;
                    break;
                }
                if (Validation.ch == '.') {
                    if (Validation.i == 0) {
                        Validation.valid = false;
                    }
                    else {
                        ++dotcount;
                    }
                }
                if (dotcount > 1) {
                    Validation.valid = false;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean validTime(String input) {
        input = input.trim();
        Validation.len = input.length();
        int hours = 0;
        int minute = 0;
        Validation.valid = true;
        if (input != null && Validation.len >= 1) {
            if (onlyTime(input) && Validation.len <= 5) {
                Validation.i = input.indexOf(":");
                if (Validation.i != 1 && Validation.i != 2 && Validation.i != -1) {
                    Validation.valid = false;
                }
                else {
                    if (Validation.i != -1) {
                        hours = Integer.parseInt(input.substring(0, Validation.i));
                        minute = Integer.parseInt(input.substring(Validation.i + 1, Validation.len));
                    }
                    else {
                        hours = Integer.parseInt(input);
                    }
                    if (hours > 12 || hours == 0 || minute >= 60) {
                        Validation.valid = false;
                    }
                }
            }
            else {
                Validation.valid = false;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyTime(String input) {
        int count = 0;
        input = input.trim();
        Validation.len = input.length();
        Validation.valid = true;
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if (Validation.valid && (Validation.ch > '9' || Validation.ch < '0') && Validation.ch != ':') {
                    Validation.valid = false;
                    break;
                }
                if (Validation.ch == ':' && ++count > 1) {
                    Validation.valid = false;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean validLastDate(final String electedDate, final String lastDate) {
        boolean valid = true;
        final int electedDay = Integer.parseInt(electedDate.substring(0, 2));
        final int electedMonth = Integer.parseInt(electedDate.substring(3, 5));
        final int electedYear = Integer.parseInt(electedDate.substring(6, electedDate.length()));
        final int lastDay = Integer.parseInt(lastDate.substring(0, 2));
        final int lastMonth = Integer.parseInt(lastDate.substring(3, 5));
        final int lastYear = Integer.parseInt(lastDate.substring(6, lastDate.length()));
        if (lastYear != electedYear + 5 || lastMonth != electedMonth || lastDay != electedDay) {
            valid = false;
        }
        return valid;
    }
    
    public static boolean greaterToDate(final String fromDate, final String toDate, final int dateDiff) {
        int flag = 0;
        try {
            final int fdd = Integer.parseInt(fromDate.substring(0, 2));
            final int fmm = Integer.parseInt(fromDate.substring(3, 5));
            final int fyy = Integer.parseInt(fromDate.substring(6, fromDate.length()));
            final int tdd = Integer.parseInt(toDate.substring(0, 2));
            final int tmm = Integer.parseInt(toDate.substring(3, 5));
            final int tyy = Integer.parseInt(toDate.substring(6, toDate.length()));
            if (fyy > tyy) {
                flag = 6;
                Validation.valid = false;
            }
            else if (fyy == tyy) {
                flag = 2;
                if (fmm > tmm) {
                    flag = 3;
                    Validation.valid = false;
                }
                if (fmm == tmm) {
                    flag = 4;
                    if (dateDiff == 1) {
                        if (fdd > tdd || fdd == tdd) {
                            flag = 5;
                            Validation.valid = false;
                        }
                    }
                    else if (dateDiff == 0 && fdd > tdd) {
                        flag = 5;
                        Validation.valid = false;
                    }
                }
            }
        }
        catch (Exception ex) {}
        return Validation.valid;
    }
    
    public static boolean floatFirstGreater(String no1, String no2) {
        no1 = no1.trim();
        no2 = no2.trim();
        final float f1 = Float.parseFloat(no1);
        final float f2 = Float.parseFloat(no2);
        return f1 > f2;
    }
    
    @Deprecated
    public int getDays(final String actualDate, final String registrationDate) {
        float hours = 0.0f;
        long m1 = 0L;
        long m2 = 0L;
        int year = Integer.parseInt(actualDate.substring(6, actualDate.length()));
        int month = Integer.parseInt(actualDate.substring(3, 5));
        int day = Integer.parseInt(actualDate.substring(0, 2));
        final Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        m1 = c.getTimeInMillis();
        year = Integer.parseInt(registrationDate.substring(6, registrationDate.length()));
        month = Integer.parseInt(registrationDate.substring(3, 5));
        day = Integer.parseInt(registrationDate.substring(0, 2));
        c.set(year, month - 1, day);
        m2 = c.getTimeInMillis();
        hours = (float)((m2 - m1) / 86400000L);
        final Float f = new Float(hours);
        final int days = f.intValue();
        return days;
    }
    
    public static boolean signedNumber(String input) {
        input = input.trim();
        int dotcount = 0;
        Validation.valid = true;
        String prefix = "";
        String postfix = "";
        int prefixvalue = 0;
        int postfixvalue = 0;
        int start = 0;
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            char ch = input.charAt(0);
            if (ch == '-' || ch == '+') {
                start = 1;
            }
            dotcount = 0;
            Validation.i = start;
            while (Validation.i < Validation.len) {
                if (!Validation.valid) {
                    break;
                }
                ch = input.charAt(Validation.i);
                if (Validation.valid && (ch > '9' || ch < '0') && ch != '.') {
                    Validation.valid = false;
                    break;
                }
                if (ch == '.') {
                    if (Validation.i == 0) {
                        Validation.valid = false;
                    }
                    else if (++dotcount == 1) {
                        prefix = input.substring(0, Validation.i);
                        postfix = input.substring(Validation.i + 1, input.length());
                        prefixvalue = Integer.parseInt(prefix);
                        postfixvalue = Integer.parseInt(postfix);
                        if (prefixvalue < 1 && postfixvalue < 1) {
                            Validation.valid = false;
                        }
                    }
                }
                if (dotcount > 1) {
                    Validation.valid = false;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static int yearsDifference(final Date a, final Date b) {
        final Calendar calendarA = Calendar.getInstance();
        final Calendar calendarB = Calendar.getInstance();
        int multiplier;
        if (b.getTime() - a.getTime() > 0L) {
            multiplier = -1;
            calendarA.setTime(b);
            calendarB.setTime(a);
        }
        else {
            multiplier = 1;
            calendarA.setTime(a);
            calendarB.setTime(b);
        }
        int years = calendarA.get(1) - calendarB.get(1);
        final int months = calendarA.get(2) - calendarB.get(2);
        final int days = calendarA.get(5) - calendarB.get(5);
        if (years > 0 && (months < 0 || (months == 0 && days < 0))) {
            --years;
        }
        return years * multiplier;
    }
    
    public static String checkValidPinCode(final String strPinCode) {
        String message = "";
        final boolean flag = false;
        if (isEmpty(strPinCode) && !onlyInteger(strPinCode)) {
            message = String.valueOf(message) + "Please enter valid pincode <br>";
        }
        if (onlyInteger(strPinCode) && !flag && strPinCode.length() != 6) {
            message = String.valueOf(message) + "Enter valid pin code of 6 digit<br>";
        }
        return message;
    }
    
    public static boolean onlyInteger1(String input) {
        final ArrayList alist = new ArrayList();
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if (Validation.valid && (Validation.ch > '9' || Validation.ch < '0')) {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyNotDigit(final String input) {
        boolean valid = true;
        char ch = ' ';
        if (input != null && input.length() > 0) {
            for (int i = 0; i < input.length(); ++i) {
                ch = input.charAt(i);
                if (ch <= '9' && ch >= '0') {
                    valid = false;
                }
            }
        }
        else {
            valid = false;
        }
        return valid;
    }
    
    public static boolean checkDecimal(final String input) {
        boolean error = true;
        final int digits = input.length();
        String check = "";
        final String check2 = "";
        final int l = 0;
        if (input.indexOf(46) != -1) {
            check = input.substring(input.indexOf(46) + 1);
            if (!onlyInteger(check)) {
                error = false;
            }
            if (onlyInteger(check) && check.length() != 2) {
                error = false;
            }
            if (input.contains("")) {
                error = false;
            }
        }
        else if (!onlyInteger(input)) {
            error = false;
        }
        return error;
    }
    
    public static boolean validDecimal(final String input) {
        boolean error = true;
        final int digits = input.length();
        String check1 = "";
        String check2 = "";
        final int l = 0;
        if (input.indexOf(46) != -1) {
            check2 = input.substring(input.indexOf(46) + 1);
            check1 = input.substring(0, input.indexOf(46));
            if (!onlyInteger(check1)) {
                error = false;
            }
            if (onlyInteger(check1) && !onlyInteger(check2)) {
                error = false;
            }
        }
        else if (!onlyInteger(input)) {
            error = false;
        }
        return error;
    }
    
    public static boolean validDecimalNew(final String input) {
        boolean error = true;
        final int digits = input.length();
        String check1 = "";
        String check2 = "";
        final int l = 0;
        if (input.indexOf(46) != -1) {
            check2 = input.substring(input.indexOf(46) + 1);
            check1 = input.substring(0, input.indexOf(46));
            final int check3 = input.indexOf(46);
            if (digits - check3 - 1 > 2) {
                return false;
            }
            if (!onlyInteger(check1)) {
                error = false;
            }
            if (onlyInteger(check1) && !onlyInteger(check2)) {
                error = false;
            }
        }
        else if (!onlyInteger(input)) {
            error = false;
        }
        return error;
    }
    
    public static int isSpChar(final String strEmailID) {
        final String strSpChar = "<>,/?';:\"[]{}=+|-&*()^%$#!\\~ ";
        int count = 0;
        for (int i = 0; i < strEmailID.length(); ++i) {
            if ("@".indexOf(strEmailID.charAt(i)) != -1) {
                ++count;
            }
        }
        for (int i = 0; i < strEmailID.length(); ++i) {
            if (count == 1 && (strSpChar.indexOf(strEmailID.charAt(i)) != -1 || strSpChar.indexOf(strEmailID.charAt(i)) == 0 || strEmailID.indexOf("_") == 0 || strEmailID.indexOf(".") == 0)) {
                return 1;
            }
        }
        return 0;
    }
    
    public static int emailID_Validation(final String strMailID) {
        final String strEmailId = strMailID.trim();
        String strMDomain = " ";
        int iAmp = 0;
        int iDot = 0;
        final int lenEmail = strEmailId.length();
        iAmp = strEmailId.indexOf("@");
        iDot = strEmailId.lastIndexOf(".");
        strMDomain = strEmailId.substring(iDot + 1, lenEmail);
        final int lenDomain = strMDomain.length();
        int index1 = 0;
        int commact = 0;
        while (true) {
            final int pos = strEmailId.indexOf("@", index1);
            if (pos == -1) {
                break;
            }
            ++commact;
            index1 = pos + 1;
        }
        if (commact > 1) {
            return 0;
        }
        if (lenEmail <= 40 && isSpChar(strEmailId) != 1 && iAmp != -1 && iDot != -1 && iAmp < iDot && lenDomain > 1 && lenDomain < 5 && iAmp != 0 && iDot != iAmp + 1) {
            return 1;
        }
        return 0;
    }
    
    public static String roundOffDecimal(String input) {
        final String check1 = "";
        String check2 = "";
        String check3 = "";
        String check4 = "";
        String check5 = "";
        int l = 0;
        check2 = input.substring(input.indexOf(46) + 1);
        check3 = input.substring(input.indexOf(46) + 2, input.indexOf(46) + 3);
        check4 = input.substring(0, input.indexOf(46) + 2);
        check5 = input.substring(0, input.indexOf(46) + 3);
        final char ch1 = check2.charAt(2);
        final char ch2 = check2.charAt(1);
        final char ch3 = check2.charAt(0);
        l = Integer.parseInt(check3);
        final int l2 = Integer.parseInt(input.substring(input.indexOf(46) + 1, input.indexOf(46) + 2));
        final int l3 = Integer.parseInt(input.substring(0, input.indexOf(46)));
        final int l4 = l3 + 1;
        final int l5 = Integer.parseInt(input.substring(input.indexOf(46) + 1, input.indexOf(46) + 2)) + 1;
        if (ch1 == '9' && ch2 == '9' && ch3 == '9') {
            input = Integer.toString(l4);
        }
        else if (ch1 == '9' && ch2 == '9' && (ch3 < '\t' || ch3 >= '\u0005')) {
            input = String.valueOf(input.substring(0, input.indexOf(46) + 1)) + Integer.toString(l2 + 1) + 0;
        }
        else if (ch2 == '9' && ch3 == '9' && ch1 >= '5') {
            input = Integer.toString(l4);
        }
        else if (ch2 == '9' && ch3 != '9' && ch1 >= '5') {
            ++l;
            input = String.valueOf(input.substring(0, input.indexOf(46) + 1)) + (l2 + 1) + 0;
        }
        else if (ch2 < '9' && ch3 == '9' && ch1 >= '5' && ch1 <= '9') {
            ++l;
            input = String.valueOf(check4) + l;
        }
        else if (ch1 < '5' && ch2 == '9' && ch3 == '9') {
            input = check5;
        }
        else if (ch3 == '9' && ch2 == '9' && ch1 >= '\u0005' && ch1 < '\t') {
            input = Integer.toString(l4);
        }
        else {
            input = check5;
        }
        return input;
    }
    
    public static boolean webSite(String input) {
        input = input.trim();
        final Pattern p = Pattern.compile("www\\.(.+)\\.(.+)");
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean emailCheck(final String str) {
        final String at = "@";
        final String dot = ".";
        final int lat = str.indexOf(at);
        final int lstr = str.length();
        final int ldot = str.indexOf(dot);
        try {
            if (str.charAt(lat - 1) == '.') {
                return false;
            }
            for (int j = lat + 1; j < ldot; ++j) {
                if (str.charAt(j) == '!' || str.charAt(j) == '@' || str.charAt(j) == '#' || str.charAt(j) == '$' || str.charAt(j) == '%' || str.charAt(j) == '^' || str.charAt(j) == '&' || str.charAt(j) == '*' || str.charAt(j) == '(' || str.charAt(j) == ')' || str.charAt(j) == '-' || str.charAt(j) == '+' || str.charAt(j) == '=') {
                    return false;
                }
            }
            for (int i = 0; i < lat; ++i) {
                if (str.charAt(i) == '!' || str.charAt(i) == '@' || str.charAt(i) == '#' || str.charAt(i) == '$' || str.charAt(i) == '%' || str.charAt(i) == '^' || str.charAt(i) == '&' || str.charAt(i) == '*' || str.charAt(i) == '(' || str.charAt(i) == ')' || str.charAt(i) == '-' || str.charAt(i) == '+' || str.charAt(i) == '=') {
                    return false;
                }
            }
            if (str.lastIndexOf(dot) == lstr - 1) {
                return false;
            }
            if (str.indexOf(at) == -1) {
                return false;
            }
            if (str.indexOf(at) == -1 || str.indexOf(at) == 0 || str.indexOf(at) == lstr) {
                return false;
            }
            if (str.indexOf(dot) == -1 || str.indexOf(dot) == 0 || str.indexOf(dot) == lstr) {
                return false;
            }
            if (str.indexOf(at, lat + 1) != -1) {
                return false;
            }
            if (str.substring(lat - 1, lat) == dot || str.substring(lat + 1, lat + 2) == dot) {
                return false;
            }
            if (str.indexOf(dot, lat + 2) == -1) {
                return false;
            }
            if (str.indexOf(" ") != -1) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean websiteCheck(final String str) {
        final String subStr = "www.";
        if (!str.startsWith(subStr)) {
            return false;
        }
        final String substr1 = str.substring(4, str.length());
        if (substr1.contains(" ")) {
            return false;
        }
        final int strIndexOfdot = str.indexOf(".", 4);
        if (strIndexOfdot < 0) {
            return false;
        }
        for (int i = 4; i < strIndexOfdot; ++i) {
            if (str.charAt(i) == '!' || str.charAt(i) == '@' || str.charAt(i) == '#' || str.charAt(i) == '$' || str.charAt(i) == '%' || str.charAt(i) == '^' || str.charAt(i) == '&' || str.charAt(i) == '*' || str.charAt(i) == '(' || str.charAt(i) == ')' || str.charAt(i) == '+' || str.charAt(i) == '=' || str.charAt(i) == '<' || str.charAt(i) == '>' || str.charAt(i) == '?' || str.charAt(i) == '/' || str.charAt(i) == ',' || str.charAt(i) == ':' || str.charAt(i) == '\"' || str.charAt(i) == ';' || str.charAt(i) == '|') {
                return false;
            }
            str.contains("");
        }
        if (str.contains("..")) {
            return false;
        }
        final int strLastIndexOfDot = str.lastIndexOf(".");
        return strLastIndexOfDot + 1 != str.length();
    }
    
    public static boolean checkDate(String input, final int grace) {
        int mm = 0;
        int dd = 0;
        int yy = 0;
        int yylen = 0;
        int curyear = 0;
        int curmonth = 0;
        int curdate = 0;
        String dd2 = "";
        String mm2 = "";
        String yy2 = "";
        boolean valid = true;
        final SimpleDateFormat sp = new SimpleDateFormat("dd-MMM-yyyy");
        final Date today = new Date();
        final Calendar cal = Calendar.getInstance();
        curdate = cal.get(5);
        curmonth = cal.get(2) + 1;
        if (grace == 1) {
            curyear = cal.get(1) + 6;
        }
        else {
            curyear = cal.get(1);
        }
        input = input.trim();
        final int len = input.length();
        String mt = "0";
        if (input.substring(2, 3).equals("-") && input.substring(6, 7).equals("-")) {
            if (onlyInteger(input.substring(0, 2)) && onlyInteger(input.substring(7, len))) {
                if (len >= 10) {
                    dd2 = input.substring(0, 2);
                    dd = Integer.parseInt(dd2);
                    mm2 = input.substring(3, 6);
                    yy2 = input.substring(7, len);
                    if (mm2.equalsIgnoreCase("Jan")) {
                        mt = "01";
                    }
                    else if (mm2.equalsIgnoreCase("Feb")) {
                        mt = "02";
                    }
                    else if (mm2.equalsIgnoreCase("Mar")) {
                        mt = "03";
                    }
                    else if (mm2.equalsIgnoreCase("Apr")) {
                        mt = "04";
                    }
                    else if (mm2.equalsIgnoreCase("May")) {
                        mt = "05";
                    }
                    else if (mm2.equalsIgnoreCase("Jun")) {
                        mt = "06";
                    }
                    else if (mm2.equalsIgnoreCase("Jul")) {
                        mt = "07";
                    }
                    else if (mm2.equalsIgnoreCase("Aug")) {
                        mt = "08";
                    }
                    else if (mm2.equalsIgnoreCase("Sep")) {
                        mt = "09";
                    }
                    else if (mm2.equalsIgnoreCase("Oct")) {
                        mt = "10";
                    }
                    else if (mm2.equalsIgnoreCase("Nov")) {
                        mt = "11";
                    }
                    else if (mm2.equalsIgnoreCase("Dec")) {
                        mt = "12";
                    }
                    else {
                        mt = "00";
                    }
                    mm = Integer.parseInt(mt);
                    yy = Integer.parseInt(yy2);
                    if (mm != 0) {
                        yylen = yy2.length();
                        if (dd > 0 && mm > 0 && yy > 0 && yylen == 4) {
                            if (onlyInteger(dd2) && onlyInteger(mt) && onlyInteger(yy2)) {
                                if (yy == curyear) {
                                    if (mm > curmonth) {
                                        valid = false;
                                    }
                                    else if (mm == curmonth && dd > curdate) {
                                        valid = false;
                                    }
                                }
                                if (valid && mm <= 12 && dd <= 31 && yy <= curyear) {
                                    if (mm == 2) {
                                        if (yy % 4 == 0 && yy % 100 == 0 && yy % 400 == 0) {
                                            if (dd > 29) {
                                                valid = false;
                                            }
                                        }
                                        else if (dd > 28) {
                                            valid = false;
                                        }
                                    }
                                    else if ((mm == 4 || mm == 6 || mm == 9 || mm == 11) && dd == 31) {
                                        valid = false;
                                    }
                                }
                                else {
                                    valid = false;
                                }
                            }
                            else {
                                valid = false;
                            }
                        }
                        else {
                            valid = false;
                        }
                    }
                    else {
                        valid = false;
                    }
                }
                else {
                    valid = false;
                }
            }
            else {
                valid = false;
            }
        }
        else {
            valid = false;
        }
        return valid;
    }
    
    public static boolean isIPError(final String input) {
        if (input.startsWith(".")) {
            return false;
        }
        if (input.contains("..") || input.contains("...")) {
            return false;
        }
        int counter = 0;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '.') {
                ++counter;
            }
        }
        if (counter != 3) {
            return false;
        }
        int first = 0;
        int second = 0;
        int third = 0;
        final int fourth = 0;
        String string1 = "";
        String string2 = "";
        String string3 = "";
        String string4 = "";
        for (int j = 0; j < input.length(); ++j) {
            first = input.indexOf(".");
            second = input.indexOf(".", first + 1);
            third = input.indexOf(".", second + 1);
        }
        string1 = input.substring(0, first);
        string2 = input.substring(first + 1, second);
        string3 = input.substring(second + 1, third);
        string4 = input.substring(third + 1, input.length());
        if (string1.length() > 3 || string2.length() > 3 || string3.length() > 3 || string4.length() > 3) {
            return false;
        }
        if (input.charAt(input.length() - 1) == '.') {
            return false;
        }
        for (int j = 0; j < input.length(); ++j) {
            if (input.charAt(j) != '0' && input.charAt(j) != '1' && input.charAt(j) != '2' && input.charAt(j) != '3' && input.charAt(j) != '4' && input.charAt(j) != '5' && input.charAt(j) != '6' && input.charAt(j) != '7' && input.charAt(j) != '8' && input.charAt(j) != '9' && input.charAt(j) != '.') {
                return false;
            }
        }
        return Integer.parseInt(string1) <= 255 && Integer.parseInt(string2) <= 255 && Integer.parseInt(string3) <= 255 && Integer.parseInt(string4) <= 255;
    }
    
    public static boolean onlyIntDash(final String input) {
        final Pattern p = Pattern.compile("[0-9 -]+");
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean compareFloat(final String frmAmount, final String toAmount) {
        boolean isLarge = false;
        try {
            final float frmAmt = Float.parseFloat(frmAmount);
            final float toAmt = Float.parseFloat(toAmount);
            if (frmAmt > toAmt) {
                isLarge = true;
            }
        }
        catch (Exception ex) {}
        return isLarge;
    }
    
    public static boolean urlCheck(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != '-' && Validation.ch != ':' && Validation.ch != '.' && Validation.ch != '/' && Validation.ch != '\\' && Validation.ch != '&' && Validation.ch != '?' && Validation.ch != '=' && Validation.ch != '_') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean compareInteger(final String str1, final String str2) {
        final int number1 = Integer.parseInt(str1);
        final int number2 = Integer.parseInt(str2);
        return number1 <= number2;
    }
    
    public static boolean compareTwoDigitInteger(final String str1, final String str2) {
        final int number1 = Integer.parseInt(str1);
        final int number2 = Integer.parseInt(str2);
        return number1 <= 99 && number2 <= 99 && number1 <= number2;
    }
    
    public static String getDate(final String dateFormat) {
        final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }
    
    public static boolean decimalNotAllowed(final String input) {
        boolean error = true;
        final int digits = input.length();
        final String check = "";
        final int l = 0;
        if (input.indexOf(46) != -1) {
            error = false;
        }
        return error;
    }
    
    public static boolean checkDecimalwith4Digits(final String input) {
        boolean error = true;
        final int digits = input.length();
        String check = "";
        String check2 = "";
        final int l = 0;
        if (input.indexOf(46) != -1) {
            check = input.substring(input.indexOf(46) + 1);
            check2 = input.substring(0, input.indexOf(46));
            if (onlyInteger(check) && onlyInteger(check2)) {
                if (Long.parseLong(check2) == 0L && Long.parseLong(check) == 0L) {
                    error = false;
                }
                else {
                    if (!onlyInteger(check)) {
                        error = false;
                    }
                    if (onlyInteger(check) && check.length() > 4) {
                        error = false;
                    }
                }
            }
            else {
                error = false;
            }
        }
        else if (!onlyInteger(input)) {
            error = false;
        }
        else if (onlyInteger(input)) {
            error = (Integer.parseInt(input) > 0);
        }
        return error;
    }
    
    public static boolean checkDate1(String input, final int grace) {
        int mm = 0;
        int dd = 0;
        int yy = 0;
        int yylen = 0;
        int curyear = 0;
        int curmonth = 0;
        int curdate = 0;
        String dd2 = "";
        String mm2 = "";
        String yy2 = "";
        boolean valid = true;
        final SimpleDateFormat sp = new SimpleDateFormat("dd-MMM-yyyy");
        final Date today = new Date();
        final String strDate = sp.format(today);
        final Calendar cal = Calendar.getInstance();
        curdate = cal.get(5);
        curmonth = cal.get(2) + 1;
        if (grace == 1) {
            curyear = cal.get(1) + 6;
        }
        else {
            curyear = cal.get(1);
        }
        input = input.trim();
        final int len = input.length();
        String mt = "0";
        if (input.substring(2, 3).equals("-") && input.substring(6, 7).equals("-")) {
            if (onlyInteger(input.substring(0, 2)) && onlyInteger(input.substring(7, len))) {
                if (len >= 10) {
                    dd2 = input.substring(0, 2);
                    dd = Integer.parseInt(dd2);
                    mm2 = input.substring(3, 6);
                    yy2 = input.substring(7, len);
                    if (mm2.equalsIgnoreCase("Jan")) {
                        mt = "01";
                    }
                    else if (mm2.equalsIgnoreCase("Feb")) {
                        mt = "02";
                    }
                    else if (mm2.equalsIgnoreCase("Mar")) {
                        mt = "03";
                    }
                    else if (mm2.equalsIgnoreCase("Apr")) {
                        mt = "04";
                    }
                    else if (mm2.equalsIgnoreCase("May")) {
                        mt = "05";
                    }
                    else if (mm2.equalsIgnoreCase("Jun")) {
                        mt = "06";
                    }
                    else if (mm2.equalsIgnoreCase("Jul")) {
                        mt = "07";
                    }
                    else if (mm2.equalsIgnoreCase("Aug")) {
                        mt = "08";
                    }
                    else if (mm2.equalsIgnoreCase("Sep")) {
                        mt = "09";
                    }
                    else if (mm2.equalsIgnoreCase("Oct")) {
                        mt = "10";
                    }
                    else if (mm2.equalsIgnoreCase("Nov")) {
                        mt = "11";
                    }
                    else if (mm2.equalsIgnoreCase("Dec")) {
                        mt = "12";
                    }
                    else {
                        mt = "00";
                    }
                    mm = Integer.parseInt(mt);
                    yy = Integer.parseInt(yy2);
                    if (mm != 0) {
                        yylen = yy2.length();
                        if (dd > 0 && mm > 0 && yy > 0 && yylen == 4) {
                            if (onlyInteger(dd2) && onlyInteger(mt) && onlyInteger(yy2)) {
                                try {
                                    final Date inputdate = sp.parse(input);
                                    final Date dtcurrent = sp.parse(strDate);
                                    if (inputdate.compareTo(dtcurrent) >= 0) {
                                        valid = false;
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace(System.out);
                                }
                                if (yy == curyear) {
                                    if (mm > curmonth) {
                                        valid = false;
                                    }
                                    else if (mm == curmonth && dd > curdate) {
                                        valid = false;
                                    }
                                }
                                if (valid && mm <= 12 && dd <= 31 && yy <= curyear) {
                                    if (mm == 2) {
                                        if (yy % 4 == 0 && yy % 100 == 0 && yy % 400 == 0) {
                                            if (dd > 29) {
                                                valid = false;
                                            }
                                        }
                                        else if (dd > 28) {
                                            valid = false;
                                        }
                                    }
                                    else if ((mm == 4 || mm == 6 || mm == 9 || mm == 11) && dd == 31) {
                                        valid = false;
                                    }
                                }
                                else {
                                    valid = false;
                                }
                            }
                            else {
                                valid = false;
                            }
                        }
                        else {
                            valid = false;
                        }
                    }
                    else {
                        valid = false;
                    }
                }
                else {
                    valid = false;
                }
            }
            else {
                valid = false;
            }
        }
        else {
            valid = false;
        }
        return valid;
    }
    
    public static boolean checkDateFor(final String dateStr) {
        final Pattern datePatt = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{4})");
        final Matcher m = datePatt.matcher(dateStr);
        if (m.matches()) {
            final int day = Integer.parseInt(m.group(1));
            final int month = Integer.parseInt(m.group(2));
            Integer.parseInt(m.group(3));
        }
        return true;
    }
    
    public static boolean onlyCharWithSpace(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != ' ' && Validation.ch != ',') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyIntegerComma(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if (Validation.valid && (Validation.ch > '9' || Validation.ch < '0') && Validation.ch != ',') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean checkSQLInjection(String str1) {
        boolean flag = true;
        String str2;
        String str3;
        String str4;
        for (str1 = str1.replaceAll("\\n", " "); str1.contains("="); str1 = str3) {
            str2 = str1.substring(0, str1.indexOf("="));
            str3 = str1.substring(str1.indexOf("=") + 1).trim();
            str4 = str2.substring(str2.lastIndexOf(" ")).trim();
            if (str3.startsWith(str4)) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    
    public static boolean onlyAlphaNumWithSpecialCharOrg(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != ':' && !Character.isWhitespace(Validation.ch)) {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean fileUploadcheck1(final String filename1) {
        final int index = filename1.indexOf(46);
        String strFileExt = "";
        if (index == -1) {
            Validation.valid = false;
        }
        else {
            final StringTokenizer st = new StringTokenizer(filename1, ".");
            while (st.hasMoreTokens()) {
                strFileExt = st.nextToken();
            }
            if (strFileExt.equalsIgnoreCase("csv") || strFileExt.equalsIgnoreCase("txt")) {
                Validation.valid = true;
            }
        }
        return Validation.valid;
    }
    
    public static boolean onlyCharIntegerUnderscore(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != ' ' && Validation.ch != '_') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean checkDate(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < '0' || Validation.ch > '9') && Validation.ch != '/' && Validation.ch != '-' && Validation.ch != ':' && Validation.ch != 'A' && Validation.ch != 'a' && Validation.ch != 'M' && Validation.ch != 'm' && Validation.ch != 'P' && Validation.ch != 'p') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyAlphaNumericWithSpecialCharsDotUnderScore(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != '.' && Validation.ch != '_' && Validation.ch != ' ') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlySpecialChars(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if (Validation.ch != '*' && Validation.ch != '^' && Validation.ch != '-' && Validation.ch != '|' && Validation.ch != '&' && Validation.ch != '#' && Validation.ch != '.' && Validation.ch != '_' && Validation.ch != ' ') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyNumericWithComma(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && Validation.ch != ',' && Validation.ch != ' ') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean checkForwardPage(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < 'A' || Validation.ch > 'Z') && (Validation.ch < 'a' || Validation.ch > 'z') && (Validation.ch < '0' || Validation.ch > '9') && Validation.ch != '.' && Validation.ch != ' ' && Validation.ch != '/' && Validation.ch != '\\') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean checkJavaObject(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != '.' && Validation.ch != ';' && Validation.ch != '@' && Validation.ch != '[' && Validation.ch != ']') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean validateEmailNew(final String value) {
        boolean flag = true;
        final Pattern p = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        final Matcher m = p.matcher(value);
        flag = m.matches();
        return flag;
    }
    
    public static boolean validateColourNew(final String value) {
        boolean flag = true;
        final Pattern p = Pattern.compile("^[a-zA-Z0-9#]+$");
        final Matcher m = p.matcher(value);
        flag = m.matches();
        return flag;
    }
    
    public static boolean validatePassword(final String value) {
        boolean flag = true;
        final Pattern p = Pattern.compile("^[a-zA-Z0-9@#$^()_.]+$");
        final Matcher m = p.matcher(value);
        flag = m.matches();
        return flag;
    }
    
    public static boolean validateHolidaySearch(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < 'A' || Validation.ch > 'Z') && (Validation.ch < 'a' || Validation.ch > 'z') && (Validation.ch < '0' || Validation.ch > '9') && Validation.ch != '/' && Validation.ch != '\\' && Validation.ch != ' ') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean validationonlyCharInt(String input) {
        input = input.trim();
        final Pattern p = Pattern.compile("[a-z A-Z 0-9 / ]+");
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean onlyAlphaNumWithSpecialCharsAdress(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != ':' && Validation.ch != '@' && Validation.ch != '#' && Validation.ch != '(' && Validation.ch != ')' && Validation.ch != '.' && Validation.ch != '_' && Validation.ch != '\\' && Validation.ch != '/' && Validation.ch != '-' && Validation.ch != '\'' && Validation.ch != ';' && Validation.ch != ',' && !Character.isWhitespace(Validation.ch)) {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean cmjValidate(String input, final boolean allowNull, final boolean allowSpace, final boolean allowSmallChar, final boolean allowCapitalChar, final boolean allowNumbers, String allowedSpecialChars) {
        String checkWithThis = "";
        if (input != null) {
            input = input.trim();
        }
        if (allowNull) {
            if (input == null || input.equals("")) {
                return true;
            }
        }
        else if (input == null || input.equals("")) {
            return false;
        }
        if (allowSpace) {
            checkWithThis = String.valueOf(checkWithThis) + " ";
        }
        if (allowSmallChar) {
            checkWithThis = String.valueOf(checkWithThis) + "a-z";
        }
        if (allowCapitalChar) {
            checkWithThis = String.valueOf(checkWithThis) + "A-Z";
        }
        if (allowNumbers) {
            checkWithThis = String.valueOf(checkWithThis) + "0-9";
        }
        if (allowedSpecialChars != null) {
            allowedSpecialChars = allowedSpecialChars.trim();
            allowedSpecialChars.replace("\\", "\\\\");
        }
        checkWithThis = "^[" + checkWithThis + allowedSpecialChars + "]+$";
        final Pattern p = Pattern.compile(checkWithThis);
        final Matcher m = p.matcher(input);
        return Validation.valid = m.matches();
    }
    
    public static boolean validateNameNew(final String value) {
        boolean flag = true;
        final Pattern p = Pattern.compile("^[a-zA-Z0-9&@() ]+$");
        final Matcher m = p.matcher(value);
        flag = m.matches();
        return flag;
    }
    
    public static boolean validateAddressNew(final String value) {
        boolean flag = true;
        final Pattern p = Pattern.compile("^[a-zA-Z0-9#,;.() ]+$");
        final Matcher m = p.matcher(value);
        flag = m.matches();
        return flag;
    }
    
    public static boolean onlyIntgerByslash(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if (Validation.valid && (Validation.ch > '9' || Validation.ch < '0') && Validation.ch != '/') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean validateAlphanumericSearch(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < 'A' || Validation.ch > 'Z') && (Validation.ch < 'a' || Validation.ch > 'z') && (Validation.ch < '0' || Validation.ch > '9') && Validation.ch != ' ') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean validateAlphanumericwithoutwhitespace(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < 'A' || Validation.ch > 'Z') && (Validation.ch < 'a' || Validation.ch > 'z') && (Validation.ch < '0' || Validation.ch > '9')) {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean alphanumericWithDotOperator(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != '.') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
    
    public static boolean onlyAlphaNumericWithUnderScoreHyphen(String input) {
        Validation.valid = true;
        input = input.trim();
        Validation.len = input.length();
        if (input != null && Validation.len >= 1) {
            Validation.i = 0;
            while (Validation.i < Validation.len) {
                Validation.ch = input.charAt(Validation.i);
                if ((Validation.ch < '0' || Validation.ch > '9') && (Validation.ch < 'a' || 'z' < Validation.ch) && (Validation.ch < 'A' || 'Z' < Validation.ch) && Validation.ch != '-' && Validation.ch != '_') {
                    Validation.valid = false;
                    break;
                }
                ++Validation.i;
            }
        }
        else {
            Validation.valid = false;
        }
        return Validation.valid;
    }
}
