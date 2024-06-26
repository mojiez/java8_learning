package com.winterbe.java8.samples.lambda;

/**
 * @author Benjamin Winterberg
 */
public class Lambda2 {

    @FunctionalInterface
    // 这里的F T是什么？ 是泛型参数 使用 <> 定义
    public static interface Converter<F, T> {
        T convert(F from);
    }

    static class Something {
        String startsWith(String s) {
            return String.valueOf(s.charAt(0));
        }
    }

    interface PersonFactory<P extends Person> {
        P create(String firstName, String lastName);
    }

    public static void main(String[] args) {
        Converter<String, Integer> integerConverter1 = (from) -> Integer.valueOf(from);
        Integer converted1 = integerConverter1.convert("123");
        System.out.println(converted1);   // result: 123


        // method reference
//        Integer类中的valueof()是一个静态方法 可以通过直接引用已有的方法，来实现接口，不需要再编写额外的lambda表达式
        Converter<String, Integer> integerConverter2 = Integer::valueOf;
        Integer converted2 = integerConverter2.convert("123");
        System.out.println(converted2);   // result: 123


        Something something = new Something();
        // 可以引用非静态的方法（直接使用类的实例:: 来引用）
        Converter<String, String> stringConverter = something::startsWith;
        String converted3 = stringConverter.convert("Java");
        System.out.println(converted3);    // result J

        // constructor reference

        PersonFactory<Person> personFactory = Person::new;
        Person person = personFactory.create("Peter", "Parker");
    }
}
