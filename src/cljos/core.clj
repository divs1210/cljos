(ns cljos.core)

(defmacro defclass [class-name & bindings]
  (let [obj-map      (into {} (partition 2 bindings))
        let-bindings (->> (get-in obj-map [:let] [])
                          (partition 2)
                          (mapcat #([(first %) `(atom ~(second %))])))
        constructor  (get-in obj-map [:init] '([] nil))
        methods      (into {} (filter #(not= (first %) :let :init) 
                                      obj-map))]
    `(defn ~class-name ~(first constructor)
       (let [~@let-bindings]
         ~@(rest ~constructor)
         (fn [method# & args#]
           (case method#
             ~@(mapcat (fn [m-name# expr#]
                         [~m-name# (do ~@expr#)])
                       methods)))))))