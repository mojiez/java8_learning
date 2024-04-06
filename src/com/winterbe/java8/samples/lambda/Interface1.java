package com.winterbe.java8.samples.lambda;

/**
 * @author Benjamin Winterberg
 */
public class Interface1 {

    // 非抽象方法实现
    interface Formula {
        // 抽象方法
        double calculate(int a);
        // 非抽象方法
        default double sqrt(int a) {
            return Math.sqrt(positive(a));
        }

        static int positive(int a) {
            return a > 0 ? a : 0;
        }
    }

    public static void main(String[] args) {
        // 这里是 一个匿名实现类
        Formula formula1 = new Formula() {
            @Override
            public double calculate(int a) {
                return sqrt(a * 100);
            }
        };

        /**
         * 非匿名实现类
         */
        // 定义一个实现了 Formula 接口的具体类
//        class MyFormula implements Formula {
//            @Override
//            public double calculate(int a) {
//                return sqrt(a * 100);
//            }
//        }
//        MyFormula formula = new MyFormula();
//        formula.calculate(100);  // 输出：100.0
//        formula.sqrt(16);        // 输出：4.0
        
        formula1.calculate(100);     // 100.0
        formula1.sqrt(-23);          // 0.0
        Formula.positive(-4);        // 0.0

//        Formula formula2 = (a) -> sqrt( a * 100);
    }

}