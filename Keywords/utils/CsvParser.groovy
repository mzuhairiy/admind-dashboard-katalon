package utils

/** Minimal RFC 4180 parser for deterministic CSV response assertions. */
class CsvParser {
    static List<List<String>> parse(String csv) {
        List<List<String>> rows = []
        List<String> row = []
        StringBuilder field = new StringBuilder()
        boolean quoted = false

        for (int index = 0; index < csv.length(); index++) {
            char character = csv.charAt(index)
            if (character == '"') {
                if (quoted && index + 1 < csv.length() && csv.charAt(index + 1) == '"') {
                    field.append('"')
                    index++
                } else {
                    quoted = !quoted
                }
            } else if (character == ',' && !quoted) {
                row << field.toString()
                field.setLength(0)
            } else if ((character == '\n' || character == '\r') && !quoted) {
                if (character == '\r' && index + 1 < csv.length() && csv.charAt(index + 1) == '\n') {
                    index++
                }
                row << field.toString()
                rows << row
                row = []
                field.setLength(0)
            } else {
                field.append(character)
            }
        }
        if (field.length() > 0 || !row.isEmpty()) {
            row << field.toString()
            rows << row
        }
        return rows
    }
}
