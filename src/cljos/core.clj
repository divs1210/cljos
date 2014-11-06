(ns cljos.core)

;-----The 'class' data structre-------------------
(defn make-class
  [type extends vars methods]
  {:vars  (or (into (extends :vars) vars)    {})
   :fns   (or (into (extends :fns ) methods) {})
   :type  type
   :super extends})

(defmacro defclass
  "Every CljOS class should extend another.
   Extend <Obj> if none other is applicable."
  [name extends vars methods]
  `(def ~name
     (~make-class (quote ~name)
                  ~extends
                  ~vars ~methods)))

;-----Bootstrapping the CljOS base class----------
(def <Obj*>
  {:vars  {}
   :fns   {:init (fn [_])}
   :type  '<Obj*>
   :super nil})

(defclass <Obj> <Obj*> {} {})

;-----Object instantiation------------------------
(defn new+
  "Instantiates a new CljOS object, which is a closure
   with access to a set of methods, and *mutable* state." 
  [class-name & args]
  (let [state (atom (class-name :vars))
        fns   (class-name :fns)
        this  (fn this* [method & argv]
                (case method
                  ;special keywords
                  :type  (class-name :type)
                  :state @state
                  :super (let [[method & args] argv]
                           (apply (partial (-> class-name :super :fns method) 
                                           this*)
                                  args))
                  :set   (let [[var val] argv]
                           (swap! state #(assoc % var val)))
                  :setf  (let [[var f & args] argv]
                           (this* :set var 
                             (apply (partial f (@state var)) args)))
                  :swap  (let [[f & args] argv]
                           (apply (partial swap! state f) args))
                  ;otherwise first check if it's
                  ;a method. If not, treat it as
                  ;a property.
                  (if (contains? fns method)
                    (apply (partial (fns method) this*) argv)
                    (get @state method))))]
    ;call constructor, and return the object
    (apply (partial (fns :init) this) args)
    this))

;-----Utility macro(s)-----------------------------
(defmacro doto+
  "Like \"doto\", but for CljOS objects."
  [obj & forms]
  (cons 'do (for [form forms]
              (if (seq? form)
                (cons obj form)
                (list obj form)))))