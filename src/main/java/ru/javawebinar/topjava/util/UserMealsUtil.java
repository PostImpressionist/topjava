package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

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

    // cycles algo
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if(meals == null) return new ArrayList<>();

        // sorting original meals list -> from oldest to newest
        meals.sort((o1, o2) -> o2.getDateTime().isBefore(o1.getDateTime()) ? 0 : -1);
        // declaring list to return
        List<UserMealWithExcess> mealWithFilteredExcesslist = new ArrayList<>();
        // map - helper to store calculated exceed values with associated dates
        Map<LocalDate, Boolean> datesMap = new TreeMap<>();
        // declaring original list shorted with incoming filters
        List<UserMeal> filteredUserMeals = new ArrayList<>();

        // initiating params to tune first cycle
        int sum = 0;
        LocalDate localDateOld = !meals.isEmpty() ? meals.get(0).getDate() : null;

        //first cycle to filter original list with UserMeal objects and to calculate exceed values and put them to map
        for (UserMeal meal : meals) {
            // if filter is true -> add to the filtered list
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                filteredUserMeals.add(meal);
            }
            // if localDateOld isn't equal the current one, then the sum of calories per this day(localDateOld) is ready
            if (localDateOld.isEqual(meal.getDate())) {
                sum += meal.getCalories();
            } else {
                sum = meal.getCalories();   // setting for sum the first value to start calculating it for new day
                localDateOld = meal.getDate(); // setting new value for localDateOld day
            }
            //forming map with dates as keys and excess as values
            datesMap.put(localDateOld, sum > caloriesPerDay);
        } // end of first cycle

        // second cycle to fill returned list
        for (UserMeal meal : filteredUserMeals) {
            mealWithFilteredExcesslist.add(new UserMealWithExcess(
                    meal.getDateTime(),
                    meal.getDescription(),
                    meal.getCalories(),
                    datesMap.get(meal.getDate())
            ));
        }// end of second cycle

        return mealWithFilteredExcesslist;  // it was not so easy to avoid O(N^2) hear...
    }

    // Optional 1
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if(meals == null) return new ArrayList<>();

        List<UserMealWithExcess> mealWithExcessListFiltered = new ArrayList<>();

        meals.stream().collect(Collectors.groupingBy(UserMeal::getDate)).forEach((date, list) -> {
            int sum = list.stream().mapToInt(UserMeal::getCalories).sum();
            list.stream()
                    .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                    .forEach(meal -> mealWithExcessListFiltered.add(new UserMealWithExcess(
                            meal.getDateTime(),
                            meal.getDescription(),
                            meal.getCalories(),
                            sum > caloriesPerDay
                    )));
        });
        return mealWithExcessListFiltered;
    }
}
