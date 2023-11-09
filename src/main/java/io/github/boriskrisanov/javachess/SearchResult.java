package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

public record SearchResult(Move bestMove, int eval, long debugPositionsEvaluated) {
}
