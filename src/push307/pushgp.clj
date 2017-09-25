(ns push307.pushgp
  (:require [push307.pushgp.crossover :refer :all])
  (:require [push307.pushgp.selection :refer :all])
  (:require [push307.pushgp.mutation :refer :all])
  (:gen-class))

  ; ; An example individual in the population
  ; ; Made of a map containing, at mimimum, a program, the errors for
  ; ; the program, and a total error
  ; (def example-individual
  ;   {:program '(3 5 integer_* "hello" 4 "world" integer_-)
  ;    :errors [8 7 6 5 4 3 2 1 0 1]
  ;    :total-error 37})

  (defn make-random-push-program
    "Creates and returns a new program. Takes a list of instructions and
    a maximum initial program size."
    [instructions max-initial-program-size]
    :STUB
    )

  (defn select-and-vary
    "Selects parent(s) from population and varies them, returning
    a child individual (note: not program). Chooses which genetic operator
    to use probabilistically. Gives 50% chance to crossover,
    25% to uniform-addition, and 25% to uniform-deletion."
    [population]
    :STUB
    )

  (defn report
    "Reports information on the population each generation. Should look something
    like the following (should contain all of this info; format however you think
    looks best; feel free to include other info).

  -------------------------------------------------------
                 Report for Generation 3
  -------------------------------------------------------
  Best program: (in1 integer_% integer_* integer_- 0 1 in1 1 integer_* 0 integer_* 1 in1 integer_* integer_- in1 integer_% integer_% 0 integer_+ in1 integer_* integer_- in1 in1 integer_* integer_+ integer_* in1 integer_- integer_* 1 integer_%)
  Best program size: 33
  Best total error: 727
  Best errors: (117 96 77 60 45 32 21 12 5 0 3 4 3 0 5 12 21 32 45 60 77)
    "
    [population generation]
    :STUB
    )

  (defn push-gp
    "Main GP loop. Initializes the population, and then repeatedly
    generates and evaluates new populations. Stops if it finds an
    individual with 0 error (and should return :SUCCESS, or if it
    exceeds the maximum generations (and should return nil). Should print
    report each generation.
    --
    The only argument should be a map containing the core parameters to
    push-gp. The format given below will decompose this map into individual
    arguments. These arguments should include:
     - population-size
     - max-generations
     - error-function
     - instructions (a list of instructions)
     - max-initial-program-size (max size of randomly generated programs)"
    [{:keys [population-size max-generations error-function instructions max-initial-program-size]}]
    :STUB
    )

