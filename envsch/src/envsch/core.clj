;; Clojure programs for book
;; Not necessarily designed to be efficient

(ns envsch.core
  (:require [clojure.math.combinatorics :as comb]
            [clojure.tools.cli :as cli]))

(def envs$ [:wet :dry])
(def traits$ [:deep :shallow])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Default values ;; 
(def fitnesses$ {:deep    {:dry 2 :wet 1}
                :shallow {:dry 1 :wet 2}})

(def env-probs$ {:dry 0.75 :wet 0.25})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; functions that do the work:

(defn env-seqs
  "Generate all possible sequences of envs of length gens."
  [gens]
  (comb/selections envs$ gens))

;; Inefficient
(defn env-seq-prob
  "Calculate the probability of an env-seq."
  [env-probs env-seq]
  (reduce * (map env-probs env-seq)))

;; No tail recursion elimination--will blow the stack if env-seq is too long.
(defn n-gens-growth
   "Given a sequence of envs and a trait, calculate the growth of that
   trait in that sequence."
   [fitnesses trait env-seq]
   (let [trait-fitnesses (trait fitnesses)]
     (reduce (fn [curr-pop-size env]
               (* curr-pop-size (trait-fitnesses env)))
             1
             env-seq)))

(defn avg-pop-size-after-n-gens
  "Calculate the expectation for the number of trait individuals after 
  gens generations, beginning from a single individual.  The expectation 
  weights sequences of environments by their probabilities."
  [env-probs fitnesses gens trait]
  (let [es (env-seqs gens)
        es-probs (map (partial env-seq-prob env-probs) es)
        growths (map (partial n-gens-growth fitnesses trait) es)]
    (reduce + (map * es-probs growths))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Command line processing

(defn record-commandline-args!
  "Temporarily store values of parameters passed on the command line."
  [args]
  (let [cli-options [["-?" "--help" "Print this help message."]
                     ["-g" "--generations" "Number of generations to run."  :parse-fn #(Integer. %)]
                     ["-p" "--prob-dry" "Probability of dry environment; subtract from 1 to get prob of wet." :parse-fn #(Double. %)]
                     ;; use first letter of trait for dry, second letter for wet:
                     ["-d" "--deep-dry" "Number of offspring of the deep trait in the dry environment." :parse-fn #(Double. %)]
                     ["-e" "--deep-wet" "Number of offspring of the deep trait in the wet environment." :parse-fn #(Double. %)]
                     ["-s" "--shallow-dry" "Number of offspring of the shallow trait in the dry environment." :parse-fn #(Double. %)]
                     ["-h" "--shallow-wet" "Number of offspring of the shallow trait in the wet environment." :parse-fn #(Double. %)]]
        usage-fmt (fn [options]
                    (let [fmt-line (fn [[short-opt long-opt desc]] (str short-opt ", " long-opt ": " desc))]
                      (clojure.string/join "\n" (concat (map fmt-line options)))))
        ;error-fmt (fn [errors] (str "The following errors occurred while parsing your command:\n\n" (apply str errors))) ; not in use
        {:keys [options arguments errors summary] :as commline} (clojure.tools.cli/parse-opts args cli-options)]
    (if (:help options)
      (do
        (println "Command line options:")
        (println (usage-fmt cli-options))
        (System/exit 0))
      commline)))


(defn -main
  "I do a whole lot."
  [& args]
  (let [commline (record-commandline-args! args)
        options (:options commline)
        gens (read-string (first (:arguments commline)))
        ;; env probabilities:
        dry-prob (:prob-dry commline)
        wet-prob (if dry-prob (- 1 dry-prob) nil)
        env-probs (merge env-probs$
                         {:dry dry-prob :wet wet-prob})
        ;; fitnesses:
        deep-dry    (:deep-dry commline)
        deep-wet    (:deep-wet commline)
        shallow-dry (:shallow-dry commline)
        shallow-wet (:shallow-wet commline)
        fitnesses (merge fitnesses$
                         {:deep    {:dry deep-dry    :wet deep-wet}
                          :shallow {:dry shallow-dry :wet shallow-wet}})]
    (run! 
      (fn [trait] (println trait (avg-pop-size-after-n-gens env-probs fitnesses gens trait)))
      traits$)))
