package nl.openminetopia.modules.chat.filter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable: a config reload builds a new filter, so async chat always sees one consistent set of lists.
 */
public final class ChatFilter {

    private static final Pattern FORMATTING = Pattern.compile("<[/!#]?[a-zA-Z0-9_:#.\\-'\" ]+>|[&§][0-9a-fk-orxA-FK-ORX]");
    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Map<Character, Character> LEET = Map.ofEntries(
            Map.entry('0', 'o'), Map.entry('1', 'i'), Map.entry('3', 'e'), Map.entry('4', 'a'),
            Map.entry('5', 's'), Map.entry('7', 't'), Map.entry('8', 'b'), Map.entry('@', 'a'),
            Map.entry('$', 's'), Map.entry('!', 'i'), Map.entry('|', 'i')
    );

    private final List<Run[]> words;
    private final List<Run[]> contains;
    private final List<String> allowed;

    public ChatFilter(List<String> words, List<String> contains, List<String> allowed) {
        this.words = toRuns(words);
        this.contains = toRuns(contains);
        this.allowed = allowed.stream().map(ChatFilter::letters).filter(s -> !s.isEmpty()).toList();
    }

    public String censor(String text) {
        List<int[]> hits = findHits(text);
        if (hits.isEmpty()) return text;

        char[] chars = text.toCharArray();
        for (int[] hit : hits) {
            int end = Character.isHighSurrogate(chars[hit[1]]) ? hit[1] + 1 : hit[1];
            for (int i = hit[0]; i <= end && i < chars.length; i++) chars[i] = '*';
        }
        return new String(chars);
    }

    public Optional<String> findProfanity(String text) {
        List<int[]> hits = findHits(text);
        if (hits.isEmpty()) return Optional.empty();
        int[] hit = hits.getFirst();
        return Optional.of(text.substring(hit[0], text.offsetByCodePoints(hit[1], 1)));
    }

    private List<int[]> findHits(String text) {
        List<int[]> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) return hits;

        Normalized n = Normalized.of(text);
        List<Token> tokens = n.tokens();

        // Leet chars on a token edge are tried both ways: "$hit" needs them, "kut!" does not.
        for (Token token : tokens) {
            int[][] candidates = {
                    {token.start, token.end}, {token.start, token.trimmedEnd},
                    {token.trimmedStart, token.end}, {token.trimmedStart, token.trimmedEnd}
            };
            for (int[] candidate : candidates) {
                if (candidate[0] < candidate[1] && matchesWord(n, candidate[0], candidate[1])) {
                    hits.add(n.originRange(candidate[0], candidate[1] - 1));
                    break;
                }
            }
        }

        List<Run> runs = n.letterRuns(0, n.length());
        for (Run[] entry : contains) {
            for (int i = 0; i + entry.length <= runs.size(); i++) {
                if (!runsMatch(runs, i, entry)) continue;
                int start = runs.get(i).normStart;
                int end = runs.get(i + entry.length - 1).normEnd;
                if (isValidContainsHit(n, tokens, start, end)) hits.add(n.originRange(start, end));
            }
        }
        return hits;
    }

    private boolean matchesWord(Normalized n, int start, int end) {
        List<Run> runs = n.letterRuns(start, end);
        for (Run[] word : words) {
            if (word.length == runs.size() && runsMatch(runs, 0, word)) return true;
        }
        return false;
    }

    /**
     * A hit inside one token counts unless an allowed word covers it. A hit over several tokens
     * ("k a n k e r", "kan ker") only counts when it starts and ends on token edges, so "ik kan kerst" stays clean.
     */
    private boolean isValidContainsHit(Normalized n, List<Token> tokens, int start, int end) {
        Token first = n.tokenAt(tokens, start);
        Token last = n.tokenAt(tokens, end);
        if (first == null || last == null) return false;

        if (first == last) {
            String value = n.text.substring(first.start, first.end);
            for (String word : allowed) {
                for (int idx = value.indexOf(word); idx >= 0; idx = value.indexOf(word, idx + 1)) {
                    if (first.start + idx <= start && first.start + idx + word.length() > end) return false;
                }
            }
            return true;
        }
        return start <= first.trimmedStart && end + 1 >= last.trimmedEnd;
    }

    private static boolean runsMatch(List<Run> text, int offset, Run[] entry) {
        for (int j = 0; j < entry.length; j++) {
            Run run = text.get(offset + j);
            if (run.letter != entry[j].letter || run.count < entry[j].count) return false;
        }
        return true;
    }

    private static List<Run[]> toRuns(List<String> entries) {
        return entries.stream()
                .map(ChatFilter::letters)
                .filter(s -> !s.isEmpty())
                .map(s -> Normalized.of(s).letterRuns(0, s.length()).toArray(Run[]::new))
                .toList();
    }

    private static String letters(String entry) {
        Normalized n = Normalized.of(entry);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n.length(); i++) {
            if (Character.isLetter(n.text.charAt(i))) builder.append(n.text.charAt(i));
        }
        return builder.toString();
    }

    private record Run(char letter, int count, int normStart, int normEnd) {
    }

    private record Token(int start, int end, int trimmedStart, int trimmedEnd) {
    }

    /**
     * Lowercased, diacritic- and leet-free copy of the input with formatting removed. Every char keeps the index
     * of the original char it came from, so hits can be censored in the original text.
     */
    private record Normalized(String text, int[] origin, BitSet leet) {

        static Normalized of(String input) {
            BitSet skip = new BitSet(input.length());
            Matcher matcher = FORMATTING.matcher(input);
            while (matcher.find()) skip.set(matcher.start(), matcher.end());

            StringBuilder builder = new StringBuilder(input.length());
            List<Integer> origin = new ArrayList<>(input.length());
            BitSet leet = new BitSet();

            for (int i = 0; i < input.length(); ) {
                int codePoint = input.codePointAt(i);
                if (!skip.get(i)) {
                    String lower = new String(Character.toChars(codePoint)).toLowerCase(Locale.ROOT);
                    String plain = MARKS.matcher(Normalizer.normalize(lower, Normalizer.Form.NFKD)).replaceAll("");
                    for (char c : plain.toCharArray()) {
                        Character mapped = LEET.get(c);
                        if (mapped != null) leet.set(builder.length());
                        builder.append(mapped != null ? mapped : c);
                        origin.add(i);
                    }
                }
                i += Character.charCount(codePoint);
            }
            return new Normalized(builder.toString(), origin.stream().mapToInt(Integer::intValue).toArray(), leet);
        }

        int length() {
            return text.length();
        }

        int[] originRange(int start, int end) {
            return new int[]{origin[start], origin[end]};
        }

        List<Token> tokens() {
            List<Token> tokens = new ArrayList<>();
            int i = 0;
            while (i < text.length()) {
                if (!Character.isLetter(text.charAt(i))) {
                    i++;
                    continue;
                }
                int start = i;
                while (i < text.length() && Character.isLetter(text.charAt(i))) i++;

                int trimmedStart = start;
                while (trimmedStart < i && leet.get(trimmedStart)) trimmedStart++;
                int trimmedEnd = i;
                while (trimmedEnd > trimmedStart && leet.get(trimmedEnd - 1)) trimmedEnd--;
                tokens.add(new Token(start, i, trimmedStart, trimmedEnd));
            }
            return tokens;
        }

        Token tokenAt(List<Token> tokens, int index) {
            for (Token token : tokens) {
                if (index >= token.start && index < token.end) return token;
            }
            return null;
        }

        List<Run> letterRuns(int start, int end) {
            List<Run> runs = new ArrayList<>();
            for (int i = start; i < end; i++) {
                char c = text.charAt(i);
                if (!Character.isLetter(c)) continue;
                if (!runs.isEmpty() && runs.getLast().letter == c) {
                    Run last = runs.removeLast();
                    runs.add(new Run(c, last.count + 1, last.normStart, i));
                } else {
                    runs.add(new Run(c, 1, i, i));
                }
            }
            return runs;
        }
    }
}
