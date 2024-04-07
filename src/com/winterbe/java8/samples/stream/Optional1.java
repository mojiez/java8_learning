package com.winterbe.java8.samples.stream;

import java.util.Optional;

/**
 * @author Benjamin Winterberg
 */
public class Optional1 {

    public static void main(String[] args) {
//        Optional<String> optional = Optional.of("danm");
        Optional<String> optional = Optional.empty();
        // 方法用于检查 Optional 对象是否包含值。在这个例子中，它返回 true，因为 Optional 对象包含了一个非 null 的值。
//        optional.isPresent();           // true
//        // get() 方法用于获取 Optional 对象中的值。在这个例子中，它返回字符串 "bam"。
//        optional.get();                 // "bam"
//        // orElse("fallback") 方法用于在 Optional 对象不包含值时提供一个默认值。在这个例子中，因为 Optional 对象包含了值，所以它返回字符串 "bam"。
        String s = optional.orElse("fallback");// "bam" // 如果没有值提供一个默认值 就是 fallback
        System.out.println(s);
////        ifPresent((s) -> System.out.println(s.charAt(0))) 方法用于在 Optional 对象包含值时执行一个操作。在这个例子中，它打印出了字符串 "bam" 的第一个字符 "b"。
//        optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
    }

}