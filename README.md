
# Implementation of LR(0) shift-reduce parser generator.
- Visit my [blog post](https://dafuqisthatblog.wordpress.com/2017/10/12/compiler-theories-parser-bottom-up-parsers-slr-lr1-lalr/) for the theories of bottom-up parsers.

## Basic info
- **OS**: Windows.
- **Language**: Java.
- **Third library**: Google Guava. The library is included in the repository. However you have to manually add the JAR file into the library yourself.
- **Java competency**: Experienced.
- **Prerequesties**: A machine that is able to run java programs and JDK 1.8 or higher ( to use GUAVA ).

## Fix bugs:
- i mistakenly thought that the first production in grammar.txt is the only initial production, but through test cases it was endowed upom me that this is not the case. We can have multiple start-symbol productions. Therefore a change has been made to allow users to foretell the parser how many start-symbol productions there are.


## What does this program do ?
- It takes in a grammar description and an input and verifies wether the input belongs to the grammar. In practice, output of parsers should be AST or parse tree, however i skipped that part since building the parse is trivial. Such functionality will be integrated in future parser projects.

**Tasks finished**:
- [x] Finish the project. ( it generates correct output )
- [X] Error report. ( shift/reduce conflicts and reduce/reduce conflicts detection )
- [ ] Reconsider the data structures and functions organization.

## The parser consists of the following classes:

-**Production**: A production has a left-hand side being a noterminal symbol ( an uppercase character ), and a right-hand side which is a combination of terminals and nonterminals ( a string ), seperated by a colon. ( for example, A:w is a production )

-**Item**: An item is  a production with a dot somewhere on the right-hand side.

-**State**: A DFA state is the result of computing closure on an item.

-**Stack**: The primary interface of the parser. Stack contains DFA states. I noticed some textbooks say that there should be interleaving occurences of states and symbols, but i figured it would just be easier to maintain the states alone and symbols can be stored in a single variable. 

-**Parser**: The main class that reads the list of productions, constructing the automata, the parsing table and dictates the behavior of the stack and errors handling.

-**Main**: The class that calls everything.

## Other notes: 

-**Test files**: 
- grammar.txt has the first line indicating the number of start symbol productions, followed by indicated start symbol productions and the remaining productions.
- input.txt contains the input.

