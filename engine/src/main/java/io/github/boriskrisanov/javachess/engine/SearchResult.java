package io.github.boriskrisanov.javachess.engine;

import io.github.boriskrisanov.javachess.engine.board.*;

public record SearchResult(Move bestMove, int eval, long debugPositionsEvaluated) {
}
