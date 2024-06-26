# Modern Java - A Guide to Java 8
_This article was originally posted on [my blog](http://winterbe.com/posts/2014/03/16/java-8-tutorial/)._

> **You should also read my [Java 11 Tutorial](https://winterbe.com/posts/2018/09/24/java-11-tutorial/) (including new language and API features from Java 9, 10 and 11).**

Welcome to my introduction to [Java 8](https://jdk8.java.net/). This tutorial guides you step by step through all new language features. Backed by(以...为基础) short and simple code samples you'll learn how to use default interface methods（默认接口方式）, lambda expressions, method references（方法引用） and repeatable annotations（可重复注解）. At the end of the article you'll be familiar with the most recent [API](http://download.java.net/jdk8/docs/api/) changes like streams流, functional interfaces函数式接口, map extensions map扩展 and the new Date API新的日期API. **No walls of text, just a bunch of commented code snippets片段. Enjoy! 没有冗长的文字，只有一堆有注释的代码！**

---

<p align="center">
 ★★★ Like this project? Leave a star, <a href="https://twitter.com/winterbe_">follow on Twitter</a> or <a href="https://www.paypal.me/winterbe">donate</a> to support my work. Thanks! ★★★
</p>

---

## Table of Contents

* [Default Methods for Interfaces](#default-methods-for-interfaces)
* [Lambda expressions](#lambda-expressions)
* [Functional Interfaces](#functional-interfaces)
* [Method and Constructor References](#method-and-constructor-references)
* [Lambda Scopes](#lambda-scopes)
  * [Accessing local variables](#accessing-local-variables)
  * [Accessing fields and static variables](#accessing-fields-and-static-variables)
  * [Accessing Default Interface Methods](#accessing-default-interface-methods)
* [Built-in Functional Interfaces](#built-in-functional-interfaces)
  * [Predicates](#predicates)
  * [Functions](#functions)
  * [Suppliers](#suppliers)
  * [Consumers](#consumers)
  * [Comparators](#comparators)
* [Optionals](#optionals)
* [Streams](#streams)
  * [Filter](#filter)
  * [Sorted](#sorted)
  * [Map](#map)
  * [Match](#match)
  * [Count](#count)
  * [Reduce](#reduce)
* [Parallel Streams](#parallel-streams)
  * [Sequential Sort](#sequential-sort)
  * [Parallel Sort](#parallel-sort)
* [Maps](#maps)
* [Date API](#date-api)
  * [Clock](#clock)
  * [Timezones](#timezones)
  * [LocalTime](#localtime)
  * [LocalDate](#localdate)
  * [LocalDateTime](#localdatetime)
* [Annotations](#annotations)
* [Where to go from here?](#where-to-go-from-here)

## Default Methods for Interfaces

**接口的默认方法**

Java 8 enables us to add non-abstract method implementations to interfaces by utilizing the `default` keyword. This feature is also known as [virtual extension methods](http://stackoverflow.com/a/24102730). 

java8允许在接口中加入非抽象的方法实现。

非抽象方法实现：在接口中定义的方法不再是抽象的，而是具有默认的方法体实现。在java8之前，接口中的方法都是抽象的。

Here is our first example:

```java
interface Formula {
    double calculate(int a);

    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}

```

Besides the abstract method `calculate` the interface `Formula` also defines the default method `sqrt`. Concrete classes only have to implement the abstract method `calculate`. The default method `sqrt` can be used out of the box.

具体类只需要实现抽象方法calculate。默认方法sqrt可以直接使用

```java
Formula formula = new Formula() {
    @Override
    public double calculate(int a) {
        return sqrt(a * 100);
    }
};

formula.calculate(100);     // 100.0
formula.sqrt(16);           // 4.0

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
```

The formula is implemented as an anonymous object. The code is quite verbose: 6 lines of code for such a simple calculation of `sqrt(a * 100)`. As we'll see in the next section, there's a much nicer way of implementing single method objects in Java 8.


## Lambda expressions

Let's start with a simple example of how to sort a list of strings in prior versions of Java:

早期版本的java 如何对字符串排序

```java
List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return b.compareTo(a);
      b compare a是降序 a compare b是升序
      //返回值为负数、零或正数，分别表示第一个对象小于、等于或大于第二个对象。在这行代码中，b.compareTo(a) 比较了 b 和 a 的大小关系，如果 b 小于 a，则返回负数；如果 b 等于 a，则返回零；如果 b 大于 a，则返回正数。
    }
});
```

The static utility method `Collections.sort` accepts a list and a comparator in order to sort the elements of the given list. You often find yourself creating anonymous comparators and pass them to the sort method.

接受一个列表和比较器

你会发现我们会经常创建一个匿名比较器，并把它作为参数传递给sort方法

Instead of creating anonymous objects all day long, Java 8 comes with a much shorter syntax, **lambda expressions**:

每天都创建匿名对象很繁琐，Java 8引入了一种更简洁的语法，**lambda表达式**：

```java
Collections.sort(names, (String a, String b) -> {
    return b.compareTo(a);
});
```

As you can see the code is much shorter and easier to read. But it gets even shorter:

```java
Collections.sort(names, (String a, String b) -> b.compareTo(a));
```

For one line method bodies you can skip both the braces `{}` and the `return` keyword. But it gets even shorter:

```java
names.sort((a, b) -> b.compareTo(a));
```

List now has a `sort` method. Also the java compiler is aware of the parameter types so you can skip them as well. Let's dive deeper into how lambda expressions can be used in the wild.

java编译器知道参数的类型所以你可以不写


## Functional Interfaces 函数式接口

How does lambda expressions fit into Java's type system? Each lambda corresponds to a given type, specified by an interface. A so called _functional interface_ must contain **exactly one abstract method** declaration. Each lambda expression of that type will be matched to this abstract method. 每个lambda表达式都会和接口中的唯一一个抽象方法进行匹配 Since default methods are not abstract you're free to add default methods to your functional interface.由于默认方法不是抽象方法，因此您可以向函数式接口中添加默认方法而不会影响其作为函数式接口的特性。

lambda表达式在java中是通过函数式接口来实现的

**函数式接口是指只包含一个抽象方法的接口。lambda 表达式可以被视为函数式接口的实例** 调用的就是这个抽象方法

只要接口只包含一个抽象方法，我们都可以使用lambda表达式

We can use arbitrary interfaces as lambda expressions as long as the interface only contains one abstract method. To ensure that your interface meet the requirements（为了确保接口满足要求，可以使用一个注解）, you should add the `@FunctionalInterface` annotation. The compiler is aware of this annotation and throws a compiler error as soon as you try to add a second abstract method declaration to the interface.

编译器知道这个注解，并且在您尝试向接口添加第二个抽象方法声明时立即抛出编译错误。

Example:

```java
@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
```

```java
// 相当于new了一个Converter实例并且用lambda表达式实现了唯一的抽象方法
Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
Integer converted = converter.convert("123");
System.out.println(converted);    // 123
```

Keep in mind that the code is also valid if the `@FunctionalInterface` annotation would be omitted.


## Method and Constructor References 方法引用和构造函数引用

The above example code can be further simplified by utilizing static method references:

通过利用静态方法引用 可以进一步简化代码

```java
Converter<String, Integer> converter = Integer::valueOf;
Integer converted = converter.convert("123");
System.out.println(converted);   // 123
```

Java 8 enables you to pass references of methods or constructors via the `::` keyword. The above example shows how to reference a static method. But we can also reference object methods:除了静态方法，也可以引用其他的方法

```java
class Something {
    String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }
}
```

```java
Something something = new Something();
Converter<String, String> converter = something::startsWith;
String converted = converter.convert("Java");
System.out.println(converted);    // "J"
```

Let's see how the `::` keyword works for constructors. First we define an example class with different constructors:

```java
class Person {
    String firstName;
    String lastName;
		// 两个不同的构造函数
    Person() {}

    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
```

Next we specify a person factory interface to be used for creating new persons:

```java
interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}
```

Instead of implementing the factory manually, we glue everything together via constructor references:

```java
// 使用Person的构造函数 来实现接口的抽象方法create
PersonFactory<Person> personFactory = Person::new;
// 自动匹配到 有两个参数的构造函数（因为create有两个参数）
Person person = personFactory.create("Peter", "Parker");
```

We create a reference to the Person constructor via `Person::new`. The Java compiler automatically chooses the right constructor by matching the signature of `PersonFactory.create`.

## Lambda Scopes lambda表达式的范围

从 lambda 表达式中访问外部作用域的变量与匿名对象非常相似。

Accessing outer scope variables from lambda expressions is very similar to anonymous objects. You can access final variables from the local outer scope as well as instance fields and static variables.

### Accessing local variables 访问局部变量

可以访问lambda表达式以外的，final变量

We can read final local variables from the outer scope of lambda expressions:

```java
final int num = 1;
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num);

stringConverter.convert(2);     // 3
```

But different to anonymous objects the variable `num` does not have to be declared final. This code is also valid:

```java
int num = 1;
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num);

stringConverter.convert(2);     // 3
```

However `num` must be implicitly final for the code to compile. The following code does **not** compile:

```java
int num = 1;
// 就是说虽然不用声明为final，但是实际上就是final的
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num);
num = 3;
```

Writing to `num` from within the lambda expression is also prohibited.

在lambda表达式内部修改final也是不行的

### Accessing fields and static variables 访问字段 和 静态变量

> 什么是字段？
>
> 字段是类或者对象中的成员变量
>
> 可以分为实例字段和静态字段

In contrast to local variables, we have both read and write access to instance fields and static variables from within lambda expressions. This behaviour is well known from anonymous objects.

```java
class Lambda4 {
    static int outerStaticNum;
    int outerNum;

    // lambda表达式可以随便访问字段（要是在类外面怎么办？）
    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            outerNum = 23;
            return String.valueOf(from);
        };

        Converter<Integer, String> stringConverter2 = (from) -> {
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }
}
```

### Accessing Default Interface Methods 访问默认接口方法

Remember the formula example from the first section? Interface `Formula` defines a default method `sqrt` which can be accessed from each formula instance including anonymous objects. This does not work with lambda expressions.

Default methods **cannot** be accessed from within lambda expressions. The following code does not compile:

// 默认接口方法不能在lambda表达式中使用

```java
// 错的 不能编译
Formula formula = (a) -> sqrt(a * 100);
```

## Built-in Functional Interfaces 内置的函数式接口

函数式接口 就是只包含一个抽象方法的接口

The JDK 1.8 API contains many built-in functional interfaces. Some of them are well known from older versions of Java like `Comparator` or `Runnable`. Those existing interfaces are extended to enable Lambda support via the `@FunctionalInterface` annotation.

But the Java 8 API is also full of new functional interfaces to make your life easier. Some of those new interfaces are well known from the [Google Guava](https://code.google.com/p/guava-libraries/) library. Even if you're familiar with this library you should keep a close eye on how those interfaces are extended by some useful method extensions.

### Predicates

一元函数 boolean类型

Predicates are boolean-valued functions of one argument. The interface contains various default methods for composing predicates to complex logical terms (and, or, negate)

这个接口包含了各种默认方法，用于将 Predicates 组合成复杂的逻辑条件（与、或、非）。

```java
Predicate<String> predicate = (s) -> s.length() > 0;

predicate.test("foo");              // true
predicate.negate().test("foo");     // false

Predicate<Boolean> nonNull = Objects::nonNull;
Predicate<Boolean> isNull = Objects::isNull;

Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isNotEmpty = isEmpty.negate();
```

### Functions

Functions accept one argument and produce a result接收一个参数并产生一个结果. Default methods can be used to chain multiple functions together (compose, andThen).

```java
Function<String, Integer> toInteger = Integer::valueOf;
有一个默认方法andThen 可以将很多函数链到一起
Function<String, String> backToString = toInteger.andThen(String::valueOf);

backToString.apply("123");     // "123"
```

### Suppliers

Suppliers produce a result of a given generic type. Unlike Functions, Suppliers don't accept arguments.

```java
Supplier<Person> personSupplier = Person::new;
personSupplier.get();   // new Person
```

### Consumers

Consumers represent operations to be performed on a single input argument.

```java
Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
greeter.accept(new Person("Luke", "Skywalker"));
```

### Comparators

Comparators are well known from older versions of Java. Java 8 adds various default methods to the interface.

java8中对Comparators这个函数式接口添加了很多默认方法

```java
Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);

Person p1 = new Person("John", "Doe");
Person p2 = new Person("Alice", "Wonderland");

comparator.compare(p1, p2);             // > 0
comparator.reversed().compare(p1, p2);  // < 0
```

## Optionals

Optionals are not functional interfaces, but nifty utilities to prevent `NullPointerException`不是函数式接口但是很实用，用来防止NPE. It's an important concept for the next section, so let's have a quick look at how Optionals work.

Optional is a simple container for a value which may be null or non-null. Think of a method which may return a non-null result but sometimes return nothing. Instead of returning `null` you return an `Optional` in Java 8.

Optional 是一个简单的容器，用于存储可能为 null 或非 null 的值。想象一个方法可能返回非 null 结果，但有时却返回空值。在 Java 8 中，您可以返回一个 Optional，而不是返回 `null`。

```java
Optional<String> optional = Optional.of("bam");

optional.isPresent();           // true
optional.get();                 // "bam"
optional.orElse("fallback");    // "bam"

optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
```

## Streams

A `java.util.Stream` represents a sequence of elements 一系列的元素 on which one or more operations can be performed可以在其上执行多个操作. Stream operations are either _intermediate_ or _terminal_ stream的操作分为中间操作和终端操作. While terminal operations return a result of a certain type 终端操作会返回一个特定类型的结果, intermediate operations return the stream itself 中间操作会返回流本身 so you can chain multiple method calls in a row. Streams are created on a source, e.g. a `java.util.Collection` like lists or sets (maps are not supported). Stream operations can either be executed sequentially or parallely.

> Streams are extremely powerful, so I wrote a separate [Java 8 Streams Tutorial](http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/). **You should also check out [Sequency](https://github.com/winterbe/sequency) as a similiar library for the web.**

Let's first look how sequential streams work. First we create a sample source in form of a list of strings:

```java
List<String> stringCollection = new ArrayList<>();
stringCollection.add("ddd2");
stringCollection.add("aaa2");
stringCollection.add("bbb1");
stringCollection.add("aaa1");
stringCollection.add("bbb3");
stringCollection.add("ccc");
stringCollection.add("bbb2");
stringCollection.add("ddd1");
```

Collections in Java 8 are extended so you can simply create streams either by calling `Collection.stream()` or `Collection.parallelStream()`. The following sections explain the most common stream operations.

### Filter

Filter accepts a predicate to filter all elements of the stream. This operation is _intermediate_ which enables us to call another stream operation (`forEach`) on the result. ForEach accepts a consumer to be executed for each element in the filtered stream. ForEach is a terminal operation. It's `void`, so we cannot call another stream operation.

```java
stringCollection
    .stream()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);

// "aaa2", "aaa1"
```

### Sorted

Sorted is an _intermediate_ operation which returns a sorted view of the stream. The elements are sorted in natural order unless you pass a custom `Comparator`.

```java
stringCollection
    .stream()
    .sorted()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);

// "aaa1", "aaa2"
```

Keep in mind that `sorted` does only create a sorted view of the stream without manipulating the ordering of the backed collection. The ordering of `stringCollection` is untouched:

**sorted方法不会改变元素的顺序！**

```java
System.out.println(stringCollection);
// ddd2, aaa2, bbb1, aaa1, bbb3, ccc, bbb2, ddd1
```

### Map

The _intermediate_ operation `map` converts each element into another object via the given function. The following example converts each string into an upper-cased string. But you can also use `map` to transform each object into another type. The generic type of the resulting stream depends on the generic type of the function you pass to `map`.

中间操作 `map` 通过给定的函数将每个元素转换为另一个对象。以下示例将每个字符串转换为大写字符串。但是您也可以使用 `map` 将每个对象转换为另一种类型。结果流的泛型类型取决于您传递给 `map` 的函数的泛型类型。

```java
stringCollection
    .stream()
    .map(String::toUpperCase)
    .sorted((a, b) -> b.compareTo(a))
    .forEach(System.out::println);

// "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"
```

### Match

Various matching operations can be used to check whether a certain predicate matches the stream. All of those operations are _terminal_ and return a boolean result.

Match操作可以用来检查某个特定的断言是否与流匹配。所有Match都是终端操作，返回一个布尔结果

```java
boolean anyStartsWithA =
    stringCollection
        .stream()
        .anyMatch((s) -> s.startsWith("a"));

System.out.println(anyStartsWithA);      // true

boolean allStartsWithA =
    stringCollection
        .stream()
        .allMatch((s) -> s.startsWith("a"));

System.out.println(allStartsWithA);      // false

boolean noneStartsWithZ =
    stringCollection
        .stream()
        .noneMatch((s) -> s.startsWith("z"));

System.out.println(noneStartsWithZ);      // true
```

#### Count

Count is a _terminal_ operation returning the number of elements in the stream as a `long`.

终端操作 返回类型为long

```java
long startsWithB =
    stringCollection
        .stream()
        .filter((s) -> s.startsWith("b"))
        .count();

System.out.println(startsWithB);    // 3
```

### Reduce

这个终端操作对流的元素进行归约，并使用给定的函数。结果是一个持有归约值的 `Optional`。

This _terminal_ operation performs a reduction on the elements of the stream with the given function. The result is an `Optional` holding the reduced value.

终端操作

```java
Optional<String> reduced =
    stringCollection
        .stream()
        .sorted()
        .reduce((s1, s2) -> s1 + "#" + s2);

reduced.ifPresent(System.out::println);
// "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"
```

## Parallel Streams

并行流

As mentioned above streams can be either sequential or parallel. Operations on sequential streams are performed on a single thread while operations on parallel streams are performed concurrently on multiple threads.

**顺序流上的操作在单个线程上执行，而并行流上的操作在多个线程上并发执行**

The following example demonstrates how easy it is to increase the performance by using parallel streams.

First we create a large list of unique elements:

```java
int max = 100 0000;
// 100w大小的list
List<String> values = new ArrayList<>(max);
for (int i = 0; i < max; i++) {
    UUID uuid = UUID.randomUUID();
    values.add(uuid.toString());
}
```

Now we measure the time it takes to sort a stream of this collection.

### Sequential Sort

```java
long t0 = System.nanoTime();

long count = values.stream().sorted().count();
System.out.println(count);

long t1 = System.nanoTime();

long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
System.out.println(String.format("sequential sort took: %d ms", millis));

// sequential sort took: 899 ms 使用 顺序流排序
```

### Parallel Sort

```java
long t0 = System.nanoTime();

long count = values.parallelStream().sorted().count();
System.out.println(count);

long t1 = System.nanoTime();

long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
System.out.println(String.format("parallel sort took: %d ms", millis));

// parallel sort took: 472 ms使用并行流排序
```

As you can see both code snippets are almost identical but the parallel sort is roughly 50% faster. All you have to do is change `stream()` to `parallelStream()`.

## Maps

Map不直接支持流， Map接口没有stream()方法

As already mentioned maps do not directly support streams. There's no `stream()` method available on the `Map` interface itself, 

但是您可以通过 `map.keySet().stream()`、`map.values().stream()` 和 `map.entrySet().stream()` 来创建基于映射的键、值或条目的专门流。

however you can create specialized streams upon the keys, values or entries of a map via `map.keySet().stream()`, `map.values().stream()` and `map.entrySet().stream()`. 

此外，Map 支持各种新的和有用的方法来执行常见的任务。

Furthermore maps support various new and useful methods for doing common tasks.

```java
Map<Integer, String> map = new HashMap<>();

for (int i = 0; i < 10; i++) {
    // 如果没有再put
    map.putIfAbsent(i, "val" + i);
}

map.forEach((id, val) -> System.out.println(val));
```

The above code should be self-explaining: `putIfAbsent` prevents us from writing additional if null checks（使用这个可以不编写额外的空值检查）; `forEach` accepts a consumer to perform operations for each value of the map.

This example shows how to compute code on the map by utilizing functions:

```java
// 如果键为3的映射存在
map.computeIfPresent(3, (num, val) -> val + num);
map.get(3);             // val33

// 如果键值为9的映射存在，就把它设为空
map.computeIfPresent(9, (num, val) -> null);
map.containsKey(9);     // false

// 如果不存在才执行
map.computeIfAbsent(23, num -> "val" + num);
map.containsKey(23);    // true

map.computeIfAbsent(3, num -> "bam");
map.get(3);             // val33
```

Next, we learn how to remove entries for a given key, only if it's currently mapped to a given value:

```java
map.remove(3, "val3");
map.get(3);             // val33

map.remove(3, "val33");
map.get(3);             // null
```

Another helpful method:

```java
map.getOrDefault(42, "not found");  // not found
```

Merging entries of a map is quite easy:

```java
map.merge(9, "val9", (value, newValue) -> value.concat(newValue));
// map.merge(9, "concat", (value, newValue) -> value.concat(newValue)) 中的第二个参数 "concat" 是指要与映射中现有值合并的值。如果映射中已经存在键为 9 的条目，则该值将与现有值合并，并将结果放回映射中。

map.get(9);             // val9

map.merge(9, "concat", (value, newValue) -> value.concat(newValue));
map.get(9);             // val9concat
```

Merge either put the key/value into the map if no entry for the key exists, or the merging function will be called to change the existing value.

Merge方法会根据键值存在性执行不同的操作：

- 如果映射中不存在指定的键，则会将指定的键值对插入到映射中。
- 如果映射中已经存在指定的键，则会调用合并函数来修改现有的值。


## Date API

Java 8 contains a brand new date and time API under the package `java.time`. The new Date API is comparable with the [Joda-Time](http://www.joda.org/joda-time/) library, however it's [not the same](http://blog.joda.org/2009/11/why-jsr-310-isn-joda-time_4941.html). The following examples cover the most important parts of this new API.

### Clock

Clock provides access to the current date and time. Clocks are aware of a timezone and may be used instead of **Clocks 考虑到了时区，可以用来替代** `System.currentTimeMillis()` to retrieve the current time in milliseconds since Unix EPOCH 来获取自 Unix EPOCH 以来的当前时间的毫秒数. Such an instantaneous（瞬时的） point （时间点） on the time-line is also represented by the class `Instant`. Instants can be used to create legacy `java.util.Date` objects.

```java
Clock clock = Clock.systemDefaultZone();
long millis = clock.millis();

Instant instant = clock.instant();
Date legacyDate = Date.from(instant);   // legacy java.util.Date
```

### Timezones

Timezones are represented by a `ZoneId`. They can easily be accessed via static factory methods静态工厂方法. Timezones define the offsets which are important to convert between instants and local dates and times.

```java
System.out.println(ZoneId.getAvailableZoneIds());
// prints all available timezone ids

ZoneId zone1 = ZoneId.of("Europe/Berlin");
ZoneId zone2 = ZoneId.of("Brazil/East");
System.out.println(zone1.getRules());
System.out.println(zone2.getRules());

// ZoneRules[currentStandardOffset=+01:00]
// ZoneRules[currentStandardOffset=-03:00]
```

### LocalTime 当地时间

LocalTime represents a time without a timezone 表示没有时区的时间, e.g. 10pm or 17:30:15. The following example creates two local times for the timezones defined above. Then we compare both times and calculate the difference in hours and minutes between both times.

```java
LocalTime now1 = LocalTime.now(zone1);
LocalTime now2 = LocalTime.now(zone2);

System.out.println(now1.isBefore(now2));  // false

long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);

System.out.println(hoursBetween);       // -3
System.out.println(minutesBetween);     // -239
```

LocalTime comes with various factory methods to simplify the creation of new instances, including parsing of time strings.

```java
LocalTime late = LocalTime.of(23, 59, 59);
System.out.println(late);       // 23:59:59

DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(Locale.GERMAN);

LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
System.out.println(leetTime);   // 13:37
```

### LocalDate

LocalDate represents a distinct date, e.g. 2014-03-11. It's immutable and works exactly analog to LocalTime. The sample demonstrates how to calculate new dates by adding or subtracting days, months or years. Keep in mind that each manipulation returns a new instance.

```java
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
LocalDate yesterday = tomorrow.minusDays(2);

LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
System.out.println(dayOfWeek);    // FRIDAY
```

Parsing a LocalDate from a string is just as simple as parsing a LocalTime:

```java
DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN);

LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
System.out.println(xmas);   // 2014-12-24
```

### LocalDateTime

LocalDateTime represents a date-time. It combines date and time as seen in the above sections into one instance. `LocalDateTime` is immutable and works similar to LocalTime and LocalDate. We can utilize methods for retrieving certain fields from a date-time:

```java
LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);

DayOfWeek dayOfWeek = sylvester.getDayOfWeek();
System.out.println(dayOfWeek);      // WEDNESDAY

Month month = sylvester.getMonth();
System.out.println(month);          // DECEMBER

long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
System.out.println(minuteOfDay);    // 1439
```

With the additional information of a timezone it can be converted to an instant. Instants can easily be converted to legacy dates of type `java.util.Date`.

```java
Instant instant = sylvester
        .atZone(ZoneId.systemDefault())
        .toInstant();

Date legacyDate = Date.from(instant);
System.out.println(legacyDate);     // Wed Dec 31 23:59:59 CET 2014
```

Formatting date-times works just like formatting dates or times. Instead of using pre-defined formats we can create formatters from custom patterns.

```java
DateTimeFormatter formatter =
    DateTimeFormatter
        .ofPattern("MMM dd, yyyy - HH:mm");

LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
String string = formatter.format(parsed);
System.out.println(string);     // Nov 03, 2014 - 07:13
```

Unlike `java.text.NumberFormat` the new `DateTimeFormatter` is immutable and **thread-safe**.

For details on the pattern syntax read [here](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html).


## Annotations 注解

Annotations in Java 8 are repeatable可重复的 可嵌套. Let's dive directly into an example to figure that out. 

First, we define a wrapper annotation which holds an array of the actual annotations:

### 注解

1. 注解的作用？

   注解是放在Java源码的类、方法、字段、参数前的一种特殊“注释”：

   ```java
   // this is a component:
   @Resource("hello")
   public class Hello {
       @Inject
       int n;
   
       @PostConstruct
       public void hello(@Param String name) {
           System.out.println(name);
       }
   
       @Override
       public String toString() {
           return "Hello";
       }
   }
   ```

   注释会被编译器直接忽略，注解则可以被编译器打包进入class文件例如，一个配置了`@PostConstruct`的方法会在调用构造方法后自动被调用

2. 如何定义一个注解？

   ![截屏2024-04-08 20.09.12](/Users/mojie/Library/Application Support/typora-user-images/截屏2024-04-08 20.09.12.png)

   

```java
@interface Hints {
    Hint[] value();
}

@Repeatable(Hints.class)
@interface Hint {
    String value();
}
```
Java 8 enables us to use multiple annotations of the same type by declaring the annotation `@Repeatable`.

### Variant 1: Using the container annotation (old school)

```java
@Hints({@Hint("hint1"), @Hint("hint2")})
class Person {}
```

### Variant 2: Using repeatable annotations (new school)

```java
@Hint("hint1")
@Hint("hint2")
class Person {}
```

Using variant 2 the java compiler implicitly sets up the `@Hints` annotation under the hood. That's important for reading annotation information via reflection.

```java
Hint hint = Person.class.getAnnotation(Hint.class);
System.out.println(hint);                   // null

Hints hints1 = Person.class.getAnnotation(Hints.class);
System.out.println(hints1.value().length);  // 2

Hint[] hints2 = Person.class.getAnnotationsByType(Hint.class);
System.out.println(hints2.length);          // 2
```

Although we never declared the `@Hints` annotation on the `Person` class, it's still readable via `getAnnotation(Hints.class)`. However, the more convenient method is `getAnnotationsByType` which grants direct access to all annotated `@Hint` annotations.


Furthermore the usage of annotations in Java 8 is expanded to two new targets:

```java
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@interface MyAnnotation {}
```

## Where to go from here?

My programming guide to Java 8 ends here. If you want to learn more about all the new classes and features of the JDK 8 API, check out my [JDK8 API Explorer](http://winterbe.com/projects/java8-explorer/). It helps you figuring out all the new classes and hidden gems of JDK 8, like `Arrays.parallelSort`, `StampedLock` and `CompletableFuture` - just to name a few.

I've also published a bunch of follow-up articles on my [blog](http://winterbe.com) that might be interesting to you:

- [Java 8 Stream Tutorial](http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/)
- [Java 8 Nashorn Tutorial](http://winterbe.com/posts/2014/04/05/java8-nashorn-tutorial/)
- [Java 8 Concurrency Tutorial: Threads and Executors](http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/)
- [Java 8 Concurrency Tutorial: Synchronization and Locks](http://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/)
- [Java 8 Concurrency Tutorial: Atomic Variables and ConcurrentMap](http://winterbe.com/posts/2015/05/22/java8-concurrency-tutorial-atomic-concurrent-map-examples/)
- [Java 8 API by Example: Strings, Numbers, Math and Files](http://winterbe.com/posts/2015/03/25/java8-examples-string-number-math-files/)
- [Avoid Null Checks in Java 8](http://winterbe.com/posts/2015/03/15/avoid-null-checks-in-java/)
- [Fixing Java 8 Stream Gotchas with IntelliJ IDEA](http://winterbe.com/posts/2015/03/05/fixing-java-8-stream-gotchas-with-intellij-idea/)
- [Using Backbone.js with Java 8 Nashorn](http://winterbe.com/posts/2014/04/07/using-backbonejs-with-nashorn/)

You should [follow me on Twitter](https://twitter.com/winterbe_). Thanks for reading!
