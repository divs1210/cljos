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

;-----Utility fn(s) and macro(s)------------------
(defn of-class? 
  "Returns true if obj is a hash-map with all and only 
   the keys required for it to qualify as an object of 
   the given CljOS class."
  [obj class-name]
  (= (set (keys (class-name :vars)))
     (set (keys obj))))

(defmacro doto+
  "Like \"doto\", but for CljOS objects.
   Returns the value of the last expression."
  [x & forms]
  (let [gx (gensym)]
    `(let [~gx ~x]
       ~@(map (fn [f]
                (if (seq? f)
                  `(~gx ~(first f) ~@(next f))
                  `(~gx ~f)))
              forms))))

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
                           (apply (-> class-name :super :fns method) 
                                  this* args))
                  :set   (let [[var val] argv]
                           (if (contains? @state var)
                             (swap! state assoc var val)
                             (throw (Exception. (str var " undefined in class " (class-name :type) ".")))))
                  :setf  (let [[var f & args] argv]
                           (this* :set var
                             (apply f (@state var) args)))
                  :swap  (let [[f & args] argv
                               new-val    (apply f @state args)]
                           (if (of-class? new-val class-name)
                             (swap! state (fn [_] new-val))
                             (throw (Exception. (str new-val " is not a valid object of class " (class-name :type) ".")))))
                  ;otherwise first check if it's
                  ;a method. If not, treat it as
                  ;a property.
                  (if (contains? fns method)
                    (apply (fns method) this* argv)
                    (get @state method))))]
    ;call constructor, and return the object
    (apply (fns :init) this args)
    this))