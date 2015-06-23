/**
 * Copyright (c) 2007-2014, Kaazing Corporation. All rights reserved.
 */
grammar Robot;

/* Parser rules */

// A robot script is comprised of a list of commands, events and barriers for
// each stream. Note that we deliberately allow empty scripts, in order to
// handle scripts which are comprised of nothing but empty lines and comments.

scriptNode
    : (propertyNode | streamNode)* EOF
    ;

propertyNode
    : k=PropertyKeyword name=propertyName value=propertyValue
    ;

propertyName
    : Name
    ;

propertyValue
    : writeValue
    ;

streamNode
    : acceptNode
    | acceptableNode
    | connectNode
    ;

acceptNode
    : k=AcceptKeyword acceptURI=location ( AsKeyword text=Name )? (OptionKeyword TransportKeyword value=location)?
      serverStreamableNode*
    ;

acceptableNode
    : k=AcceptedKeyword ( text=Name )? streamableNode+
    ;

connectNode
    : k=ConnectKeyword (AwaitKeyword barrier=Name ConnectKeyword)? connectURI=location (OptionKeyword TransportKeyword value=location)? streamableNode+
    ;

serverStreamableNode
    : barrierNode 
    | serverEventNode
    | serverCommandNode
    | optionNode
    ;
    
optionNode 
    : readOptionNode
    | writeOptionNode
    ;

writeOptionNode: 
    k=WriteKeyword OptionKeyword name=MaskKeyword value=writeValue;

readOptionNode: 
    k=ReadKeyword OptionKeyword name=MaskKeyword value=writeValue;

serverCommandNode
    : unbindNode
    | closeNode
    ;

serverEventNode
    : openedNode
    | boundNode
    | childOpenedNode
    | childClosedNode
    | unboundNode
    | closedNode
    ;

streamableNode
    : barrierNode
    | eventNode
    | commandNode
    | optionNode
    ;

commandNode
    : writeNode
    | writeFlushNode
    | writeCloseNode
    | closeNode
    | writeHttpContentLengthNode
    | writeHttpHeaderNode
    | writeHttpHostNode
    | writeHttpMethodNode
    | writeHttpParameterNode
    | writeHttpRequestNode
    | writeHttpStatusNode
    | writeHttpVersionNode
    ;

eventNode
    : openedNode
    | boundNode
    | readNode
    | readClosedNode
    | disconnectedNode
    | unboundNode
    | closedNode
    | connectedNode
    | readHttpHeaderNode
    | readHttpMethodNode
    | readHttpParameterNode
    | readHttpVersionNode
    | readHttpStatusNode
    ;

barrierNode
    : readAwaitNode
    | readNotifyNode
    | writeAwaitNode
    | writeNotifyNode
    ;

closeNode
    : k=CloseKeyword
    ;

writeFlushNode: 
    k=WriteKeyword FlushKeyword;

writeCloseNode: 
    k=WriteKeyword CloseKeyword;

disconnectNode
    : k=DisconnectKeyword
    ;

unbindNode
    : k=UnbindKeyword
    ;

writeNode
    : k=WriteKeyword writeValue+
    ;

childOpenedNode
    : k=ChildKeyword OpenedKeyword
    ;

childClosedNode
    : k=ChildKeyword ClosedKeyword
    ;

boundNode
    : k=BoundKeyword
    ;

closedNode
    : k=ClosedKeyword
    ;

connectedNode
    : k=ConnectedKeyword
    ;

disconnectedNode
    : k=DisconnectedKeyword
    ;

openedNode
    : k=OpenedKeyword
    ;

readClosedNode: 
    k=ReadKeyword ClosedKeyword;

readNode
    : k=ReadKeyword matcher+
    ;

unboundNode
    : k=UnboundKeyword
    ;

readAwaitNode
    : k=ReadKeyword AwaitKeyword barrier=Name
    ;

readNotifyNode
    : k=ReadKeyword NotifyKeyword barrier=Name
    ;
    
writeAwaitNode
    : k=WriteKeyword AwaitKeyword barrier=Name
    ;

writeNotifyNode
    : k=WriteKeyword NotifyKeyword barrier=Name
    ;

readHttpHeaderNode
    : k=ReadKeyword HttpHeaderKeyword name=literalText (HttpMissingKeyword | matcher+)
    ;

writeHttpHeaderNode
    : k=WriteKeyword HttpHeaderKeyword name=literalText writeValue+
    ;

writeHttpContentLengthNode
    : k=WriteKeyword HttpHeaderKeyword HttpContentLengthKeyword
    ;

writeHttpHostNode
    : k=WriteKeyword HttpHeaderKeyword HttpHostKeyword
    ;

readHttpMethodNode
    : k=ReadKeyword HttpMethodKeyword method=matcher
    ;

writeHttpMethodNode
    : k=WriteKeyword HttpMethodKeyword method=writeValue
    ;

readHttpParameterNode
    : k=ReadKeyword HttpParameterKeyword name=literalText matcher+
    ;

writeHttpParameterNode
    : k=WriteKeyword HttpParameterKeyword name=literalText writeValue+
    ;

readHttpVersionNode
    : k=ReadKeyword HttpVersionKeyword version=matcher
    ;

writeHttpVersionNode
    : k=WriteKeyword HttpVersionKeyword version=writeValue
    ;

readHttpStatusNode
    : k=ReadKeyword HttpStatusKeyword code=matcher reason=matcher
    ;

writeHttpRequestNode
    : k=WriteKeyword HttpRequestKeyword form=writeValue
    ;

writeHttpStatusNode
    : k=WriteKeyword HttpStatusKeyword code=writeValue reason=writeValue
    ;

matcher
    : exactTextMatcher
    | exactBytesMatcher
    | regexMatcher
    | expressionMatcher
    | fixedLengthBytesMatcher
    | variableLengthBytesMatcher
    ;

exactTextMatcher
    : text=TextLiteral
    ;

exactBytesMatcher
    : bytes=BytesLiteral
    | byteLiteral=ByteLiteral
    | shortLiteral=TwoByteLiteral
    | longLiteral=(SignedDecimalLiteral | DecimalLiteral) 'L'
    | intLiteral=(SignedDecimalLiteral | DecimalLiteral)
    ;

regexMatcher
    :  regex=RegexLiteral
    ;

expressionMatcher
    : expression=ExpressionLiteral
    ;

fixedLengthBytesMatcher
    : '[0..' lastIndex=DecimalLiteral ']'
    | '([0..' lastIndex=DecimalLiteral ']' capture=CaptureLiteral ')'
    | '[(' capture=CaptureLiteral '){' lastIndex=DecimalLiteral '}]'
    /*
     * TODO: If I use lexer rules here for example:
     *
     *   '(' ByteKeyword capture=CaptureLiteral ')'
     *
     *   Then it will not parse "(byte:var)" It will only parse if there is a space in between, "(byte :var)". How come?
     */
    |  '(byte' byteCapture=CaptureLiteral ')'
    |  '(short' shortCapture=CaptureLiteral ')'
    |  '(int' intCapture=CaptureLiteral ')'
    |  '(long' longCapture=CaptureLiteral ')'
    ;
    
variableLengthBytesMatcher
    : '[0..' length=ExpressionLiteral ']'
    | '([0..' length=ExpressionLiteral ']' capture=CaptureLiteral ')'
    ;

writeValue
    : literalText
    | literalBytes
    | expressionValue
    ;

literalText
    : text=TextLiteral
    ;

literalBytes
    : bytes=BytesLiteral
    ;

expressionValue
    : expression=ExpressionLiteral
    ;

uriValue
    : uri=URILiteral
    ;

location
    : uriValue
    | expressionValue
    ;

SignedDecimalLiteral
    :  Plus DecimalLiteral
    |  Minus DecimalLiteral
//    |  DecimalLiteral
    ;

OptionKeyword: 'option';

MaskKeyword: 'mask';

TransportKeyword
    : 'transport'
    ;

ShortKeyword
    : 'short'
    ;

IntKeyword
    : 'int'
    ;

ByteKeyword
    : 'byte'
    ;

LongKeyword
    : 'long'
    ;

AcceptKeyword
    : 'accept'
    ;

AcceptedKeyword
    : 'accepted'
    ;

AsKeyword
    : 'as'
    ;

AwaitKeyword
    : 'await'
    ;

BindKeyword
    : 'bind'
    ;

BoundKeyword
    : 'bound'
    ;

ChildKeyword
    : 'child'
    ;

CloseKeyword
    : 'close'
    ;

ClosedKeyword
    : 'closed'
    ;

ConnectKeyword
    : 'connect'
    ;

ConnectedKeyword
    : 'connected'
    ;

DisconnectKeyword
    : 'disconnect'
    ;

DisconnectedKeyword
    : 'disconnected'
    ;

FlushKeyword
    : 'flush'
    ;

NotifyKeyword
    : 'notify'
    ;

OpenedKeyword
    : 'opened'
    ;

PropertyKeyword
    : 'property'
    ;

ReadKeyword
    : 'read'
    ;

UnbindKeyword
    : 'unbind'
    ;

UnboundKeyword
    : 'unbound'
    ;

WriteKeyword
    : 'write'
    ;

HttpContentLengthKeyword
    : 'content-length'
    ;

HttpHeaderKeyword
    : 'header'
    ;
    
HttpHostKeyword
    : 'host'
    ;

HttpMethodKeyword
    : 'method'
    ;

HttpMissingKeyword
    : 'missing'
    ;

HttpParameterKeyword
    : 'parameter'
    ;

HttpRequestKeyword
    : 'request'
    ;

HttpResponseKeyword
    : 'response'
    ;

HttpStatusKeyword
    : 'status'
    ;

HttpVersionKeyword
    : 'version'
    ;

// URI cannot begin with any of our data type delimiters, and MUST contain a colon.
URILiteral
    : Letter (Letter | '+')+ ':'
      (Letter | ':' | ';' | '/' | '=' | '.' | DecimalLiteral | '?' | '&' | '%' | '-' | ',' | '*')+
//      ~('"' | '/' | ']' | '}')
    ;

CaptureLiteral
    : ':' Identifier
    ;

ExpressionLiteral
    : '${' ~('}' | '\r' | '\n')+ '}'
    ;

RegexLiteral
  : '/' PatternLiteral '/'
// ( RegexNamedGroups+ '/' )?
  ;

//RegexNamedGroups
//  :  '(' CaptureLiteral RegexNamedGroups* ')'
//  ;

fragment
PatternLiteral
    : (~('/' | '\r' | '\n') | '\\' '/')+
    ;

BytesLiteral
    : '[' (' ')? (ByteLiteral (' ')*)+ ']'
    ;

ByteLiteral
    : HexPrefix HexDigit HexDigit
    ;

TwoByteLiteral
    : HexPrefix HexDigit HexDigit HexDigit HexDigit
    ;

fragment
HexLiteral
    : HexPrefix HexDigit+
    ;

fragment
HexPrefix
    : '0'  ('x' | 'X')
    ;

fragment
HexDigit
    : (Digit | 'a'..'f' | 'A'..'F')
    ;


Plus
    : '+'
    ;

Minus
    : '-'
    ;

DecimalLiteral
    : Number
    ;

fragment
Number
   : Digit+
   ;

fragment
Digit
    : '0'..'9'
    ;

TextLiteral
    : '"' (EscapeSequence | ~('\\' | '\r' | '\n' | '"'))+ '"'
    ;
    
// Any additions to the escaping need to be accounted for in
// org.kaazing.k3po.lang.parserScriptParseStrategy.escapeString(String toEscape);
fragment
EscapeSequence
    : '\\' ('b' | 'f' | 'r' | 'n' | 't' | '\"' | '\'' | '\\' )
    ;

Name
    : Identifier
    ;

fragment
Identifier
    : Letter (Digit | Letter)*
    ;

fragment
Letter
    : '\u0024'
    | '\u0041'..'\u005a'
    | '\u005f'
    | '\u0061'..'\u007a'
    | '\u00c0'..'\u00d6'
    | '\u00d8'..'\u00f6'
    | '\u00f8'..'\u00ff'
    | '\u0100'..'\u1fff'
    | '\u3040'..'\u318f'
    | '\u3300'..'\u337f'
    | '\u3400'..'\u3d2d'
    | '\u4e00'..'\u9fff'
    | '\uf900'..'\ufaff'
    ;

WS    : (' ' | '\r' | '\n' | '\t' | '\u000C')+ -> skip;

LineComment
    : '#' ~('\n' | '\r')* '\r'? '\n' -> skip;
