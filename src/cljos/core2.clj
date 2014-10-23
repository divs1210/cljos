(ns cljos.core2)

(defn class+ [type extends vars methods]
  {:vars  vars
   :fns   methods
   :type  type
   :super extends})

(defmacro defclass [name [extends] vars methods]
  `(def ~name
     (~class+ ~(keyword name) ~extends ~vars ~methods)))

(defn new+ [class-name & args]
  (let [state (atom (class-name :vars))
        fns   (class-name :fns)]
    (fn this [method & argv]
      (case method
        :state @state
        (if (contains? fns method)
          (apply (partial (fns method) this) argv)
          (get-in @state argv))))))

(defclass <Obj> [nil]
  {}
  {:init (fn [_])})