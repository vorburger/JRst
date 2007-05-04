====================
 |reStructuredText|
====================
-------------------------------------------------
 Markup Syntax and Parser Component of Docutils_
-------------------------------------------------

:Date: $Date$

.. contents::

reStructuredText is an easy-to-read, what-you-see-is-what-you-get
plaintext markup syntax and parser system.  It is useful for in-line
program documentation (such as Python docstrings), for quickly
creating simple web pages, and for standalone documents.
reStructuredText is designed for extensibility for specific
application domains.  The reStructuredText parser is a component of
Docutils_.  reStructuredText is a revision and reinterpretation of the
StructuredText_ and Setext_ lightweight markup systems.

The primary goal of reStructuredText is to define and implement a
markup syntax for use in Python docstrings and other documentation
domains, that is readable and simple, yet powerful enough for
non-trivial use.  The intended purpose of the markup is the conversion
of reStructuredText documents into useful structured data formats.

See statemachine.py_ for an example of a Python module fully
documented using reStructuredText.


User Documentation
==================

- `A ReStructuredText Primer`__ (HTML file, or `text source`__).
- `Quick reStructuredText`__ (user reference)
- `reStructuredText Cheat Sheet`__ (text only; 1 page for syntax, 1
  page directive & role reference)

Users who have questions or need assistance with Docutils or
reStructuredText should post a message to the Docutils-users_ mailing
list.

__ docs/user/rst/quickstart.html
__ docs/user/rst/quickstart.txt
__ docs/user/rst/quickref.html
__ docs/user/rst/cheatsheet.txt
.. _Docutils-users: docs/user/mailing-lists.html#docutils-users


Reference Documentation
=======================

- `An Introduction to reStructuredText`__ (includes the Goals__ and
  History__ of reStructuredText)
- `reStructuredText Markup Specification`__
- `reStructuredText Directives`__
- `reStructuredText Interpreted Text Roles`__

__ docs/ref/rst/introduction.html
__ docs/ref/rst/introduction.html#goals
__ docs/ref/rst/introduction.html#history
__ docs/ref/rst/restructuredtext.html
__ docs/ref/rst/directives.html
__ docs/ref/rst/roles.html


Developer Documentation
=======================

- `A Record of reStructuredText Syntax Alternatives`__
- `Problems With StructuredText`__

__ docs/dev/rst/alternatives.html
__ docs/dev/rst/problems.html


How-To's
--------

- `Creating reStructuredText Directives`__
- `Creating reStructuredText Interpreted Text Roles`__

__ docs/howto/rst-directives.html
__ docs/howto/rst-roles.html


Try it Online
=============

If you want to try reStructuredText out without downloading Docutils,
you can use the `reStructuredText online renderer`__.  Thanks to Jiri
Barton for `setting it up`__!

__ http://www.hosting4u.cz/jbar/rest/rest.html
__ http://www.hosting4u.cz/jbar/rest/about.html

.. _Docutils: index.html
.. _StructuredText:
   http://dev.zope.org/Members/jim/StructuredTextWiki/FrontPage/
.. _Setext: mirror/setext.html
.. _statemachine.py: docutils/statemachine.py

.. |reStructuredText| image:: rst.png


..
   Local Variables:
   mode: indented-text
   indent-tabs-mode: nil
   sentence-end-double-space: t
   fill-column: 70
   End:
