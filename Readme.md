## Clojure Object System
------------------------
### An OOP system in Clojure
Leiningen - [cljos "1.1.0-SNAPSHOT"]

#### Why this heresy?
CljOS (Clojure Object System) is a simple system that mimics OOP to ease transition from Java. You really shouldn't be OOPing in Clojure. Clojure is a brilliant functional language, and it would be best to use it as such. However, I have heard that MIT undergrads used to get implementing OO Sytems on top of Scheme as homework, and I wanted to take up the challenge in Clojure.

#### What it is not
* CljOS is *not* a Clojure port of CLOS, or any other existing OO system.

#### Usage
Here's a Stack implemented in CljOS:

```clojure
(defclass <Stack> <Obj>
  {:seq []}
  {:init (fn [this & xs]
           (this :set :seq (vec xs)))
   :push (fn [this x]
           (this :setf :seq conj x))
   :pop  (fn [this]
           (let [x (last (this :seq))]
             (this :setf :seq pop)
             x))})
```

which can be used in the following manner:
```clojure
(def s (new+ <Stack> 1 2))
(s :seq)    ;=> [1 2]
(s :push 3) ;=> {:seq [1 2 3]}
(s :push 4) ;=> {:seq [1 2 3 4]}
(s :pop)    ;=> 4
(s :state)  ;=> {:seq [1 2 3]}
```

##### Note
* :setf stands for 'set with fn' and updates the given var.
* :state can be used to get the object in the form of a Clojure data structure. No serialization required!
* :super can be used to access the super-class's methods, including :init.
* There is no need for getters. If :var should be accecced via a method, just turn it into one.
* All objects are automatically thread-safe!

#### License
CljOS has been released under the EPL 1.0 license.
