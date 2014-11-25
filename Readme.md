## Clojure Object System
------------------------
### A simple, dynamic, single-inheritance OOP system in Clojure
Leiningen - `[cljos "1.3.0-SNAPSHOT"]`

#### Why this heresy?
CljOS (Clojure Object System) is a simple system that mimics OOP to ease transition from Java. You really shouldn't be OOPing in Clojure. Clojure is a brilliant functional language, and it would be best to use it as such. However, I have heard that MIT undergrads used to get implementing OO Sytems on top of Scheme as homework, and I wanted to take up the challenge in Clojure.

#### Usage
Here's a Stack implemented in CljOS:

```clojure
(defclass <Stack> <Obj>
  {:seq '(), :size nil}
          
  {:init (fn [this size]
           (this :set :size size))
   
   :push (fn [this x]
           (if (< (this :seq count) (this :size))
             (this :setf :seq conj x)
             (throw (Exception. "Stack full!"))))
   
   :pop  (fn [this]
           (if (> (this :seq count) 0)
             (let [x (this :seq first)]
               (this :setf :seq pop)
               x)
             (throw (Exception. "Stack empty!"))))})
```

which can be used in the following manner:
```clojure
(def s (new+ <Stack> 3))
(s :seq)    ;=> ()
(s :push 1) ;=> {:seq (1), :size 3}
(s :push 2) ;=> {:seq (2 1), :size 3}
(s :push 3) ;=> {:seq (3 2 1), :size 3}
(s :pop)    ;=> 3
(s :state)  ;=> {:seq (2 1), :size 3}
```

though it would be better to use the `doto+` macro:
```clojure
(doto+ (new+ <Stack> 3)
  (:push 1)
  (:push 2)
  (:push 3)
  :pop :state)  ;=> {:seq (2 1), :size 3}
```

##### Note
* `:setf` stands for 'set with fn' and updates the given var.
* `:state` can be used to get the object in the form of a Clojure data structure. No serialization required!
* `:swap` works like `swap!`, and updates the `:state`.
* `:type` gets the name of the class of the object.
* `:super` can be used to access the super-class's methods, including `:init`.
* All objects are automatically thread-safe!

#### License
(C) Divyansh Prakash. CljOS is released under the EPL 1.0 license.