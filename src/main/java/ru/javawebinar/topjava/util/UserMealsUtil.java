package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

    }

    // Cycles algo
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDates = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesPerDates.merge(meal.getDate(), meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealWithExcessFilteredList = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                mealWithExcessFilteredList.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                        meal.getCalories(), caloriesPerDates.get(meal.getDate()) > caloriesPerDay));
            }
        }

        return mealWithExcessFilteredList;
    }

    // Optional 1
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDates =
                meals.stream().collect((Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories))));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesPerDates.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    // Optional 2
    public static List<UserMealWithExcess> filteredByStreamsOptional(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate)).values().stream()
                .map(k -> k.stream()
                        .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                        .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                                meal.getCalories(), k.stream().mapToInt(UserMeal::getCalories).sum() > caloriesPerDay))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList()).stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
