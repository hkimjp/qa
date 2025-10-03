(ns qa.boundary.readers
  (:require
   [next.jdbc.sql :refer [insert! query]]
   [qa.boundary.utils :refer [ds-opt]]))

(defn create-reader [db login page number]
  (insert! (ds-opt db)
           :readers
           {:login login :page page :number number}))

(defn fetch-readers [db page number since]
  (let [ret (query
             (ds-opt db)
             ["select login from readers
               where page=? and number=? and read_at > ?::date"
              page number since])]
    ret))

(defn fetch-readers-on-date [db date]
  (let [ret (query
             (ds-opt db)
             ["select distinct(login) from readers
               where DATE(read_at)=DATE(?)" date])]
    ret))
