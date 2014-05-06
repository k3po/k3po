grammar NamedGroupPattern;

options {
  k=2;
}

@lexer::header {
package com.kaazing.robot.lang.regex;
}

@parser::header {
package com.kaazing.robot.lang.regex;

import java.util.Collections;
import java.util.regex.Pattern;
import com.kaazing.robot.lang.regex.NamedGroupPattern;

}

// Disable automatic error recovery
@members {

    @Override
    public Object recoverFromMismatchedSet(IntStream pattern,
                                           RecognitionException re,
                                           BitSet follow)
        throws RecognitionException {

        throw re;
    } 

    @Override
	  protected Object recoverFromMismatchedToken(IntStream input,
                                                int ttype,
                                                BitSet follow)
        throws RecognitionException {

        throw new MismatchedTokenException(ttype, input);
    } 
}
   
namedGroupPattern returns [NamedGroupPattern value]
  :     { List<String> groupNames = new ArrayList<String>(); }
  ForwardSlash expression=regex[groupNames] ForwardSlash 
    { $value = new NamedGroupPattern(Pattern.compile($expression.text), groupNames); }
  ;

// Problem Can't do one or more so: .*(?<name>.*) and (?<name1>.*)(?<name2>.*) do not parse
regex  [List<String> groupNames]
  : (namedGroupProduction[groupNames]
        | RegexLiteral    )
  ;
  
namedGroupProduction [List<String> groupNames]
  : 
        name=NamedGroup regex[groupNames]* RightParen
        {
          $groupNames.add($name.text.substring(1));
        }  
    ;
    
  
NamedGroup returns [String name]:
  StartCaptureGroup id=Identifier GreaterThan {
    $name=$id.text;
  };

//*************In Progress
//regex  [List<String> groupNames]
//  : atom
//   ;
//
//atom
//  :
//  | capture
//  | non_capture
//  | RegexLiteral
//  ;
//
//
//capture
// : '(' '?' '<' Identifier '>' regex ')'
// | '(' regex ')'
// ;
//
//non_capture
// : '(' '?' ':' regex ')' 
// | '(' '?' '|' regex ')'
// | '(' '?' '>' regex ')'
// ;


fragment  
StartCaptureGroup: '(?<';

fragment
GreaterThan: '>';

fragment
LessThan: '<';
 
ForwardSlash: '/';

LeftParen: '(';
 
//QuestionMark: '?';

RightParen: ')';
 
 
// How do I express a regex literal that is not name capturing  other groups with and w/o ? 
RegexLiteral
  : ( ~( ForwardSlash | BackSlash | LeftParen | RightParen | '?' | CR | LF | StartCaptureGroup) 
      | BackSlash ~( CR | LF )
    )+
    | GroupingRegex
  ;
 
fragment  
GroupingRegex
  : '(' RegexLiteral RightParen
    | '(' '?' ~LessThan RegexLiteral RightParen
  ;
  
//RegexLiteral
//  : ( options {greedy=false;}
//    : PatternNonTerminal
//    )*
//    PatternTerminal
//  ;

//CaptureLiteral
//    : Colon Identifier
//    ;
//
//fragment
//PatternTerminal
//  : PatternCharacters
//  ;
//
//fragment
//PatternNonTerminal
//  : PatternQuantifiable ( PatternQuantifiers )?
//  | PatternBoundaryMatchers
//  | LeftParen ( PatternNonCapturing )? PatternNonTerminal RightParen
//  ;
//
//fragment
//PatternQuantifiable
//  : PatternCharacterClasses
//  | PatternBackReferences
//  | PatternQuotedText
//  ;
//
//fragment
//PatternQuotedText
//  : '\\Q' 
//    (options {greedy=false;}
//      : .* '\\E'
//    )
//  ;
//
//fragment
//PatternBackReferences
//  : '\\0'
//  | '\\1'
//  | '\\2'
//  | '\\3'
//  | '\\4'
//  | '\\5'
//  | '\\6'
//  | '\\7'
//  | '\\8'
//  | '\\9'
//  ;
//
//fragment
//PatternNonCapturing
//  : '?:'
//  | '?='
//  | '?!'
//  | '?<='
//  | '?<!'
//  | '?>'
//  ;
//
//fragment
//PatternCharacters
//  : PatternCharacter+
//  ;
//
//fragment
//PatternCharacter
//  : Letter
//  | ':'
//  | ' '
//  | '\\\\'
//  | '\\0' Digit ( Digit ( Digit )? )?
//  | '\\x' HexDigit HexDigit
//  | '\\u' HexDigit HexDigit HexDigit HexDigit
//  | '\\t'
//  | '\\n'
//  | '\\r'
//  | '\\f'
//  | '\\a'
//  | '\\e'
//  | '\\c' Letter
//	;
//
//fragment
//PatternCharacterClasses
//  : ('[' | '[^')  PatternCharacterClass+ ( PatternCharacterClassIntersection )? PatternCharacterClasses? ']'
//  | '.'
//  | '\\d'
//  | '\\D'
//  | '\\s'
//  | '\\S'
//  | '\\w'
//  | '\\W'
//  | PatternPosixCharacterClass 
//  | PatternJavaCharacterClass
//  ;
//
//fragment
//PatternCharacterClassIntersection
//  : '&&'
//  ;
//
//fragment
//PatternPosixCharacterClass
//  : '\\p{Lower}'
//  | '\\p{Upper}'
//  | '\\p{ASCII}'
//  | '\\p{Alpha}'
//  | '\\p{Digit}'
//  | '\\p{Alnum}'
//  | '\\p{Punct}'
//  | '\\p{Graph}'
//  | '\\p{Print}'
//  | '\\p{Blank}'
//  | '\\p{Cntrl}'
//  | '\\p{XDigit}'
//  | '\\p{Space}'
//  ;
//
//fragment
//PatternJavaCharacterClass
//  : '\\p{javaLowerCase}'
//  | '\\p{javaUpperCase}'
//  | '\\p{javaWhitespace}'
//  | '\\p{javaMirrored}'
//  ;
//
//fragment
//PatternUnicodCharacterClass
//  : '\\p{InBasicLatin}'
//  // TODO: more unicode block names
//  | '\\p{InGreek}'
//  | '\\p{C}'
//  | '\\p{Cc}'
//  | '\\p{Cf}'
//  | '\\p{Cn}'
//  | '\\p{Co}'
//  | '\\p{Cs}'
//  | '\\p{L}'
//  | '\\p{LC}'
//  | '\\p{Ll}'
//  | '\\p{Lm}'
//  | '\\p{Lo}'
//  | '\\p{Lt}'
//  | '\\p{Lu}'
//  | '\\p{M}'
//  | '\\p{Mc}'
//  | '\\p{Me}'
//  | '\\p{Mn}'
//  | '\\p{N}'
//  | '\\p{Nd}'
//  | '\\p{Nl}'
//  | '\\p{No}'
//  | '\\p{P}'
//  | '\\p{Pd}'
//  | '\\p{Pe}'
//  | '\\p{Pf}'
//  | '\\p{Pi}'
//  | '\\p{Po}'
//  | '\\p{Ps}'
//  | '\\p{S}'
//  | '\\p{Sc}'
//  | '\\p{Sk}'
//  | '\\p{Sm}'
//  | '\\p{So}'
//  | '\\p{Z}'
//  | '\\p{Zl}'
//  | '\\p{Zp}'
//  | '\\p{Zs}'
//  ;
//
//fragment
//PatternCharacterClass
//  : Letter ( '-' Letter | Letter* )
//  ;
//
//fragment
//PatternBoundaryMatchers
//  : '^'
//  | '$'
//  | '\\b'
//  | '\\B'
//  | '\\A'
//  | '\\G'
//  | '\\Z'
//  | '\\z'
//  ;
//
//fragment
//PatternQuantifiers
//  : '?'
//  | '??'
//  | '?+'
//  | '*'
//  | '*?'
//  | '*+'
//  | '+'
//  | '+?'
//  | '++'
//  | '{' Digit+ ( ',' Digit* )? ( '}' | '}?' | '}+' )
//  ;

fragment     
Letter
    : '\u0041'..'\u005a'
    | '\u005f'
    | '\u0061'..'\u007a'
    | '\u00d8'..'\u00f6'
    ;

fragment
Digit: '0'..'9';

//fragment
//HexDigit: Digit | 'a'..'f' | 'A'..'F';
//  
fragment
CR: '\r';

fragment
LF: '\n';

fragment
FF: '\u000C';

fragment
TAB: '\t';

fragment
BackSlash: '\\';
//
//fragment
//Colon: ':';
//
//fragment
//EscapedForwardSlash: BackSlash ForwardSlash; 
//
//fragment
//EscapedBackSlash: BackSlash BackSlash;

fragment
Identifier: Letter (Digit | Letter)*;
 
WS: (' ' | CR | LF | TAB | FF)+
    { $channel = HIDDEN; };
