(ns cljos.core)

(defn class* [vars methods]
  {:vars    vars
   :methods (into {:init (fn [& args])}
                  methods)})

(defmacro defclass [name vars methods]
  `(defn ~name []
     (~class* ~vars ~methods)))

(defn =<
  "Get property of obj" 
  [obj property]
  (-> obj :vars property))

(defn => 
  "Call method on obj"
  [obj method & args]
  (apply (partial (-> obj :methods method) obj) args))