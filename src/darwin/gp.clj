(ns darwin.gp
  (:require [darwin.push.instructions :refer [ins]])
  (:require [darwin.push.utilities :refer :all])
  (:require [darwin.gp.utilities :refer :all])
  (:require [darwin.gp.crossover :refer :all])
  (:require [darwin.gp.selection :refer :all])
  (:require [darwin.gp.mutation :refer :all])
  (:require [darwin.push.generation :refer :all])
  (:require [darwin.plush.generation :refer :all])
  (:require [darwin.graphics.plotter :refer :all])
  (:gen-class))

;; TODO: move into run-gp parameters
(def event-percentage-add 7)
(def event-percentage-mutate 7)
(def event-percentage-del 7)

(defn select-and-vary
  "Selects parent(s) from population and varies them, returning
  a child individual (note: not program). Chooses which genetic operator
  to use probabilistically."
  [genomic instructions literals percent-literals population]
  (let [v (rand-int 100)
        getter (if genomic :genome :program)
        add-op (if genomic uniform-addition-genome uniform-addition)
        mut-op (if genomic uniform-mutation-genome uniform-mutation)]
    (prepare-individual
      {getter
        (cond
          (< v 60) (uniform-crossover
                     (getter (tournament-selection population 30))
                     (getter (tournament-selection population 30)))
          (< v 70) (uniform-deletion
                     event-percentage-del
                     (getter (tournament-selection population 30)))
          (< v 80) (add-op
                     instructions
                     literals
                     percent-literals
                     event-percentage-add
                     (getter (tournament-selection population 30)))
          :else (mut-op
                  instructions
                  literals
                  percent-literals
                  event-percentage-mutate
                  (getter (tournament-selection population 30)))
          )
       })))

(defn best-fit
  "takes population and determines best function fitness"
  [population]
  (reduce min (map :total-error population)))

(defn median
  "return median"
  [numbers]
  (if (empty? numbers) nil
    (nth numbers (quot (count numbers) 2))))

(defn average
  "return average"
  [numbers]
    (quot (apply + numbers) (count numbers)))

(defn best-n-errors
  "returns lowest n errors in population"
  [pop n]
  (take n (sort (map :total-error pop)))
)

(defn lowest-size
  "Returns the length of the shortest program in a population of indivudals"
  [population]
  ;returns value 0-100
  (apply min
    (map count
      (map :program population))))

(defn fill-state
  "takes population and creates list of values"
  [pop gen]
  { :points-fit  (best-fit pop)
 ;;   :points-behavior (behavior-diversity pop)
    :average-fitness (best-fit pop)
    :best-size (count (:program (best-overall-fitness pop)))
    :generation gen })

(defn print-many-ln
  "Prints args to stdout in a user-friendly way."
  [& args]
  (println (apply str (map print-str args))))

(defn report
  "Reports information on a generation."
  [population generation-num]
   (let [current-state (fill-state population generation-num)
         best (best-overall-fitness population)]
    ;; plot data points
    (add-pt current-state :points-fit line-color-1)
    ; (add-pt current-state :points-behavior line-color-2)
    (add-pt current-state :average-fitness line-color-3)
    (add-pt current-state :best-size line-color-4)

    ;; print stats to the console
    (print-many-ln "------------------------------------")
    (print-many-ln "        Report for Generation " generation-num)
    (print-many-ln "------------------------------------")
    (print-many-ln "Best individual: " (:program best))
    (print-many-ln " -> errors: " (:errors best))
    (print-many-ln " -> total error: " (:total-error best))
    (print-many-ln " -> size: " (count (:program best)))
    (print-many-ln "Best 20 errors: " (best-n-errors population 20))))

(defn population-has-solution
  "Returns true if population has a program with zero error.
   False otherwise."
  [population]
  (not (nil? (find-list zero? (map :total-error population)))))

(defn make-generations
  "Returns a lazily-evaluated, Aristotelian infinite list
   representing all countable generations."
  [genomic
   population-size
   instrs
   literals
   percent-literals
   max-initial-program-size
   min-initial-program-size]
  (iterate
    (fn [population]
      (repeatedly
        population-size
        #(select-and-vary genomic instrs literals percent-literals population)))
    (repeatedly
      population-size
       #((if genomic generate-random-genome generate-random-program)
          instrs
          literals
          percent-literals
          max-initial-program-size
          min-initial-program-size))))

(defn run-gp
  "Main GP loop. Initializes the population, and then repeatedly
  generates and evaluates new populations. Stops and returns :SUCCESS
  if it finds an individual with 0 error, or if it exceeds the maximum
  generations it returns nil. Print reports each generation.
  --
  Takes one argument: a map containing the core parameters to
  push-gp. The map's keys should include:
   - population-size
   - max-generations
   - testcases (a list of test cases)
   - instructions (a list of instructions)
   - literals (a list of literals)
   - number-inputs (the number of inputs the program will take)
   - max-initial-program-size (max size of randomly generated programs)
   - min-initial-program-size (minimum size of randomly generated programs)
   - initial-percent-literals (how much of randomly generated programs should be literals, a float from 0.0 to 1.0)"
  [{:keys [population-size max-generations testcases error-function instructions number-inputs literals max-initial-program-size min-initial-program-size initial-percent-literals genomic]}]
  (start-plotter)
  (let [all-inputs (take number-inputs ins) ; generate in1, in2, in3, ...
        gens (take
               max-generations
               (make-generations
                 genomic
                 population-size
                 (concat all-inputs instructions)
                 literals ;; THINK ON: should inputs be considered literals
                          ;; or instructions?
                 initial-percent-literals
                 max-initial-program-size
                 min-initial-program-size))
        tested-gens (map #(map (fn [x] (run-tests (prepare-individual x) testcases)) %) gens)
        result (find-list
                 population-has-solution
                 (map-indexed
                   #(do
                     (report %2 (inc %1))
                     %2)
                   tested-gens))]
    (if (nil? result) nil :SUCCESS)))
