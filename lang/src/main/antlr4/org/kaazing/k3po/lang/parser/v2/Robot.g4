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
    : PropertyKeyword name=propertyName value=propertyValue
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
    : AcceptKeyword
      (AwaitKeyword await=Name)?
      acceptURI=location (AsKeyword as=Name)?
      (NotifyKeyword notify=Name)?
      acceptOption*
      serverStreamableNode*
    ;

acceptOption
    : OptionKeyword name=Name value=writeValue
    ;

acceptableNode
    : AcceptedKeyword ( text=Name )? streamableNode+
    ;

connectNode
    : ConnectKeyword
      (AwaitKeyword await=Name ConnectKeyword?)?
      connectURI=location
      connectOption*
      streamableNode+
    ;

connectOption
    : OptionKeyword name=Name value=writeValue
    ;

serverStreamableNode
    : barrierNode 
    | serverEventNode
    | serverCommandNode
    | optionNode
    ;
    
optionNode 
    : readOptionMaskNode
    | readOptionOffsetNode
    | writeOptionMaskNode
    | writeOptionOffsetNode
    | writeOptionHttpChunkExtensionNode
    | readOptionHttpChunkExtensionNode
    ;

readOptionMaskNode
    : ReadKeyword OptionKeyword name=MaskKeyword value=writeValue
    ;

readOptionOffsetNode
    : ReadKeyword OptionKeyword name=OffsetKeyword value=writeValue
    ;

readOptionHttpChunkExtensionNode
    : ReadKeyword OptionKeyword name=ChunkExtensionKeyWord value=writeValue
    ;

writeOptionMaskNode
    : WriteKeyword OptionKeyword name=MaskKeyword value=writeValue
    ;

writeOptionOffsetNode
    : WriteKeyword OptionKeyword name=OffsetKeyword value=writeValue
    ;

writeOptionHttpChunkExtensionNode
    : WriteKeyword OptionKeyword name=ChunkExtensionKeyWord value=writeValue
    ;

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
    | writeHttpChunkTrailerNode
    | writeHttpHostNode
    | writeHttpMethodNode
    | writeHttpParameterNode
    | writeHttpRequestNode
    | writeHttpStatusNode
    | writeHttpVersionNode
    | abortNode
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
    | readHttpChunkTrailerNode
    | readHttpMethodNode
    | readHttpParameterNode
    | readHttpVersionNode
    | readHttpStatusNode
    | abortedNode
    ;

barrierNode
    : readAwaitNode
    | readNotifyNode
    | writeAwaitNode
    | writeNotifyNode
    ;

closeNode
    : CloseKeyword
    ;

writeFlushNode: 
    WriteKeyword FlushKeyword;

writeCloseNode: 
    WriteKeyword CloseKeyword;

disconnectNode
    : DisconnectKeyword
    ;

unbindNode
    : UnbindKeyword
    ;

writeNode
    : WriteKeyword writeValue+
    ;

childOpenedNode
    : ChildKeyword OpenedKeyword
    ;

childClosedNode
    : ChildKeyword ClosedKeyword
    ;

boundNode
    : BoundKeyword
    ;

closedNode
    : ClosedKeyword
    ;

connectedNode
    : ConnectedKeyword
    ;

disconnectedNode
    : DisconnectedKeyword
    ;

openedNode
    : OpenedKeyword
    ;

abortNode
    : AbortKeyword
    ;

abortedNode
    : AbortedKeyword
    ;

readClosedNode: 
    ReadKeyword ClosedKeyword;

readNode
    : ReadKeyword matcher+
    ;

unboundNode
    : UnboundKeyword
    ;

readAwaitNode
    : ReadKeyword AwaitKeyword barrier=Name
    ;

readNotifyNode
    : ReadKeyword NotifyKeyword barrier=Name
    ;
    
writeAwaitNode
    : WriteKeyword AwaitKeyword barrier=Name
    ;

writeNotifyNode
    : WriteKeyword NotifyKeyword barrier=Name
    ;

readHttpHeaderNode
    : ReadKeyword HttpHeaderKeyword name=literalText (HttpMissingKeyword | matcher+)
    ;

readHttpChunkTrailerNode
    : ReadKeyword HttpChunkTrailerKeyword name=literalText (HttpMissingKeyword | matcher+)
    ;

writeHttpHeaderNode
    : WriteKeyword HttpHeaderKeyword name=literalText writeValue+
    ;

writeHttpChunkTrailerNode
    : WriteKeyword HttpChunkTrailerKeyword name=literalText writeValue+
    ;

writeHttpContentLengthNode
    : WriteKeyword HttpHeaderKeyword HttpContentLengthKeyword
    ;

writeHttpHostNode
    : WriteKeyword HttpHeaderKeyword HttpHostKeyword
    ;

readHttpMethodNode
    : ReadKeyword HttpMethodKeyword method=matcher
    ;

writeHttpMethodNode
    : WriteKeyword HttpMethodKeyword method=writeValue
    ;

readHttpParameterNode
    : ReadKeyword HttpParameterKeyword name=literalText matcher+
    ;

writeHttpParameterNode
    : WriteKeyword HttpParameterKeyword name=literalText writeValue+
    ;

readHttpVersionNode
    : ReadKeyword HttpVersionKeyword version=matcher
    ;

writeHttpVersionNode
    : WriteKeyword HttpVersionKeyword version=writeValue
    ;

readHttpStatusNode
    : ReadKeyword HttpStatusKeyword code=matcher reason=matcher
    ;

writeHttpRequestNode
    : WriteKeyword HttpRequestKeyword form=writeValue
    ;

writeHttpStatusNode
    : WriteKeyword HttpStatusKeyword code=writeValue reason=writeValue
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
    | intLiteral=(SignedDecimalLiteral | DecimalLiteral)
    | longLiteral=(SignedDecimalLiteral | DecimalLiteral) 'L'
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
    : literalURI
    | literalText
    | literalBytes
    | literalByte
    | literalShort
    | literalInteger
    | literalLong
    | expressionValue
    ;

literalURI
    : literal=URILiteral
    ;

literalText
    : literal=TextLiteral
    ;

literalBytes
    : literal=BytesLiteral
    ;

literalByte
    : literal=ByteLiteral
    ;

literalShort
    : literal=TwoByteLiteral
    ;

literalInteger
    : literal=(SignedDecimalLiteral | DecimalLiteral)
    ;

literalLong
    : literal=(SignedDecimalLiteral | DecimalLiteral) 'L'
    ;

expressionValue
    : expression=ExpressionLiteral
    ;

location
    : literalURI
    | expressionValue
    ;

SignedDecimalLiteral
    :  Plus DecimalLiteral
    |  Minus DecimalLiteral
//    |  DecimalLiteral
    ;

MaskKeyword: 'mask';

OffsetKeyword : 'offset';

OptionKeyword: 'option';

ChunkExtensionKeyWord: 'chunkExtension';

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

AbortKeyword
    : 'abort'
    ;

AbortedKeyword
    : 'aborted'
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

HttpChunkTrailerKeyword
    : 'trailer'
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
    | '\'' (EscapeSequence | ~('\\' | '\r' | '\n' | '\''))+ '\''
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
