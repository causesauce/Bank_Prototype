# Bank_Prototype
It is a program handling bank accounts. The program reads a file consisting of
lines of three kinds:

1. Representing creation of a new account:
	JohnN John Novak 2000
New account is created with identifier JohnN, owner John Novak and initial
deposit 2000.

2. Representing deposit/withdrawal:
	JohnN 500
or
	JohnN -200
The balance of the account with identifier JohnN is increased by 500 (decreased
by 200, if the amount is negative).

3. Representing a transfer between accounts:
	JohnN MaryM 500
means that John is sending money to Marry, so the John's balance is decreased
by 500 and that of Mary increased by 500.


The data in any line may be erroneous (for example, its format does not correspond
to any of the formats described above, or there are insufficient funds on an account for
a given withdrawal or transfer etc.); if this happens, the offending line is ignored and
an appropriate information is appended to a log filele (the offending line, its number
in the input file and a description of the reason of the failure).
