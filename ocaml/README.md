OCaml programs and scripts
===

### wrightfisherPDFs.exe: 

Macintosh executable that creates Wright-Fisher plots in the form of PDF
files.  You will need to use this from a command line in a terminal
window.  Enter `wrightfisherPDFs.exe -?` to get help.

### WrightFisherSelN1000.sh:

Script that runs wrightfisherPDFS.exe with parameters to create the
N=1000 Wright-Fisher plots in chapter 1 of the book.

### WrightFisherSelN100.sh:

Script that runs wrightfisherPDFS.exe with parameters to create the
N=100 Wright-Fisher plots in chapter 1 of the book.

---

The following instructions are tenative and probably need revison.
(They are more likely to work on Linux and MacOS than Windows, but
there should be a way to build this program on Windows.)

To build this from scratch for Linux, Windows, etc., first install
`ocaml` (a big job, but in theory not difficult). You will also need to
install `opam`.   Then use opam to install `dune`.  Information about
OCaml and `opam` can be found at http://ocaml.org and
https://ocamlverse.github.io .  (I last compiled the executable using *OCaml* 4.06.1
and *jbuilder* 1.0+beta20.  jbuilder has subsequently been renamed to *dune*, and there
have been some significant changes in both OCaml, dune, and the libraries on which
my code depends, so you may have to install old versions of packages to get this to
work without modification.  However, if you are on a Mac, you should be able to run
this executable inside a MacOS Terminal application window; you shouldn't need anything
else.)

Then install the imprecise-evolution repository from
https://github.com/mars0i/imprecise-evolution .

If you have `make` (a common programming tool) configured properly, you
should just be able to enter `make` in the imprecise-evolution
directory.  (I believe this will cause opam to install a lot of
libraries that you will need, but if not, contact me for help.)

If all goes well, a new wrightfisherPDFs.exe executable should appear in
_build/default/src/bin/ .
