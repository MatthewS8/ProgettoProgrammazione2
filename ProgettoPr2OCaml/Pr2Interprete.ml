type ide = string;;

type exp =
    Eint of int
  | Ebool of bool
  | Estring of string
  | Den of ide
  | Prod of exp * exp
  | Sum of exp * exp
  | Diff of exp * exp
  | Eq of exp * exp
  | Minus of exp
  | IsZero of exp
  | Or of exp * exp
  | And of exp * exp
  | Not of exp
  | Ifthenelse of exp * exp * exp
  | Let of ide * exp * exp
  | Fun of ide * exp
  | FunCall of exp * exp
  | Letrec of ide * exp * exp
  | Dict of (ide * exp) list
  | Insert of ide * exp * exp
  | Delete of exp * ide
  | HasKey of ide * exp
  | Iterate of exp * exp
  | Fold of exp * exp
  | Filter of ide list * exp;;


(* environment *)

type 't env = ide -> 't;;

let emptyenv (v: 't) = function x -> v;;

let applyenv (r : 't env) (i : ide) = r i;;

let bind (r : 't env) (i : ide) (v : 't) =
  function x -> if x = i then v
    else applyenv r x;;


(* tipi esprimibili *)

type evT =
    Int of int
  | Bool of bool
  | String of string
  | Unbound
  | FunVal of evFun
  | RecFunVal of ide * evFun
  | DictVal of (ide * evT) list
and evFun = ide * exp * evT env;;


(* type checking *)

let rec typecheck (s : string) (v : evT) : bool =
  match s with
  | "int" -> (match v with
      | Int(_) -> true
      | _ -> false)
  | "bool" -> (match v with
      | Bool(_) -> true
      | _ -> false)
  | _ -> failwith("not a valid type") 

;;

(* funzioni primitive *)

let prod x y =
  if (typecheck "int" x) && (typecheck "int" y)
  then (match (x,y) with
      | (Int(n),Int(u)) -> Int(n*u)
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let sum x y =
  if (typecheck "int" x) && (typecheck "int" y)
  then (match (x,y) with
      | (Int(n),Int(u)) -> Int(n+u)
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let diff x y =
  if (typecheck "int" x) && (typecheck "int" y)
  then (match (x,y) with
      | (Int(n),Int(u)) -> Int(n-u)
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let eq x y =
  if (typecheck "int" x) && (typecheck "int" y)
  then (match (x,y) with
      | (Int(n),Int(u)) -> Bool(n=u)
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let minus x =
  if (typecheck "int" x)
  then (match x with
      | Int(n) -> Int(-n)
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let iszero x =
  if (typecheck "int" x)
  then (match x with
      | Int(n) -> Bool(n = 0)
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let vel x y =
  if (typecheck "bool" x) && (typecheck "bool" y)
  then (match (x,y) with
      | (Bool(b),Bool(e)) -> (Bool(b || e))
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let et x y =
  if (typecheck "bool" x) && (typecheck "bool" y)
  then (match (x,y) with
      | (Bool(b),Bool(e)) -> Bool(b&&e)
      | _ -> failwith("Error in applying function"))
  else failwith("Type error");;

let non x =
  if (typecheck "bool" x)
  then (match x with
      | Bool(true) -> Bool(false)
      | Bool(false) -> Bool(true)
      | _ -> failwith("Type error"))
  else failwith("Type error");;

let rec isIn key list = 
  match list with
  | []-> false
  | (k, v) :: tail -> if k = key then true else isIn k tail
;;

let rec isDictionary list =
  match list with
  | [] -> true
  | (k, v) :: tail -> if isIn k tail = true then false else isDictionary tail
;;
    
    
(* interprete *)

let rec eval (e : exp) (r : evT env) : evT =
  match e with
  | Eint n -> Int n
  | Ebool b -> Bool b
  | Estring s -> String s
  | IsZero a -> iszero (eval a r)
  | Den i -> applyenv r i
  | Eq(a, b) -> eq (eval a r) (eval b r)
  | Prod(a, b) -> prod (eval a r) (eval b r)
  | Sum(a, b) -> sum (eval a r) (eval b r)
  | Diff(a, b) -> diff (eval a r) (eval b r)
  | Minus a -> minus (eval a r)
  | And(a, b) -> et (eval a r) (eval b r)
  | Or(a, b) -> vel (eval a r) (eval b r)
  | Not a -> non (eval a r)
  | Ifthenelse(a, b, c) ->
      let g = (eval a r) in 
      if (typecheck "bool" g) then (if g = Bool(true) then (eval b r) else (eval c r))
      else failwith ("Non boolean guard")
  | Let(i, e1, e2) -> eval e2 (bind r i (eval e1 r))
  | Fun(i, a) -> FunVal(i, a, r)
  | FunCall(f, eArg) ->
      let fClosure = (eval f r) in
      (match fClosure with
       | FunVal(arg, fBody, fDecEnv) -> eval fBody (bind fDecEnv arg (eval eArg r))
       | RecFunVal(g, (arg, fBody, fDecEnv)) ->
           let aVal = (eval eArg r) in
           let rEnv = (bind fDecEnv g fClosure) in
           let aEnv = (bind rEnv arg aVal) in
           eval fBody aEnv
       | _ -> failwith("Non functional value"))
  | Letrec(f, funDef, letBody) ->
      (match funDef with
         Fun(i, fBody) -> let r1 = (bind r f (RecFunVal(f, (i, fBody, r)))) in
           eval letBody r1 |
         _ -> failwith("Non functional def"))
  | Dict(dict) -> 
      let rec evalDict (l : (ide * exp) list) (r : evT env) : (ide * evT) list =
        match l with
        | [] -> []
        | (k1, v1)::t -> (k1, eval v1 r)::(evalDict t r)
      in DictVal(evalDict dict r)
          
  | Insert(key, value, dict) ->
      (match eval dict r with
       | DictVal d -> 
          (* se la chiave non è gia presente ritorna un dizionario con il nuovo elemento (key, value) 
             altrimenti sostituisce l'occorrenza attuale con il nuovo valore*)
           let rec insert (key : ide) (value : evT) (dict : (ide * evT) list) : (ide * evT) list =
             match dict with
             | [] -> (key, value)::[]
             | (k1, v1)::t -> if (key = k1) then (k1, value)::t else (k1, v1)::(insert key value t)
           in DictVal(insert key (eval value r) d)
       | _ -> failwith("Not a dictionary"))
  | Delete(dict, key) ->
      (match eval dict r with
       | DictVal d ->
           let rec delete (key : ide) (dict : (ide * evT) list) : (ide * evT) list =
             match dict with
             | [] -> []
             | (k1, v1)::t -> if (key = k1) then t else (k1, v1)::(delete key t)
           in DictVal(delete key d)
       | _ -> failwith("Not a dictionary"))
  | HasKey(key, dict) ->
      (match eval dict r with
       | DictVal d -> 
           let rec contains (key : ide) (dict : (ide * evT) list) : bool =
             match dict with
             | [] -> false
             | (k1, _)::t -> if (key = k1) then true else contains key t
           in Bool(contains key d)
       | _ -> failwith("Not a dictionary"))
  | Iterate(funct, dict) ->
      (match eval funct r, dict with
       | FunVal(_, _, _), Dict d ->
          (* restituisce un nuovo dizionario 
           a cui è stata applicata la f ad ogni elemento *)
           let rec apply (f : exp) (dict : (ide * exp) list) (r : evT env) : (ide * evT) list =
             match dict with
             | [] -> []
             | (k1, v1)::t -> (k1, eval (FunCall(f, v1)) r)::(apply f t r)
           in DictVal(apply funct d r)
       | _ -> failwith("Not a dictionary"))
  | Fold(funct, dict) ->
      (match eval funct r, dict with
       | FunVal(_, _, _), Dict d ->
          (* calcola il valore ottenuto applicando la f 
           a tutti gli elementi del dizionario in modo sequeanziale*)
           let rec fold (f : exp) (dict : (ide * exp) list) (acc : evT) (r : evT env) : evT =
             match dict with
             | [] -> acc
             | (_, v1)::t -> match acc, (eval (FunCall(f, v1)) r) with
               | (Int(u), Int(v)) -> fold f t (Int(u+v)) r
               | _ -> failwith("Error in applying function")
           in fold funct d (Int(0)) r
       | _ -> failwith("Not a dictionary"))
  | Filter(keylist, dict) ->
      (match eval dict r with
       | DictVal d ->
          (* returns a subset of the dictionary dict containing the keys that are also contained in the list l *)
           let rec filter (l : ide list) (dict : (ide * evT) list) : (ide * evT) list =
             match dict with
             | [] -> []
             | (k1, v1)::t -> if (List.mem k1 l) then (k1, v1)::(filter l t) else filter l t
           in DictVal(filter keylist d)
       | _ -> failwith("Not a dictionary")) 
;;


(* Test *)

(* Creazione ambiente, inizialmente vuoto *)
let myEnv = emptyenv Unbound;;

let myDict = Dict([
    ("mele",   Eint(430));
    ("banane", Eint(312));
    ("arance", Eint(525));
    ("pere",   Eint(217))
  ]);;
eval myDict myEnv;;

(* Insert -> inserisce correttamente *)
eval (Insert("prugne", Eint(300), myDict)) myEnv;;

(* Insert chiave esistente -> aggiorna il valore *)
eval (Insert("mele", Eint(550), myDict)) myEnv;; 

(* Delete *)
eval (Delete(myDict, "banane")) myEnv;;

(* Delete con chiave non esistente *)
eval (Delete(myDict, "pesche")) myEnv;; 

(* HasKey *)
eval (HasKey("arance", myDict)) myEnv;;

(* HasKey con chiave non esistente *)
eval (HasKey("prugne", myDict)) myEnv;;

let f = Fun("y", Sum(Den "y", Eint 1));;

(* Iterate, funzione incremento (+1) *)
eval (Iterate(f, myDict)) myEnv;; 

(* Fold, funzione somma *)
eval (Fold(f, myDict)) myEnv;;

(* Filter *)
eval (Filter(["mele"; "arance"], myDict)) myEnv;;

(* Filter con chiave inesistente (prugne) *)
eval (Filter(["mele";"prugne"], myDict)) myEnv;;

(* Filter con una lista vuota *)
eval (Filter([], myDict)) myEnv;;