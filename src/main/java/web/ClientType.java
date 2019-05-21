package web;

/**
 * Encapsulates the different states of a WhiteRaven player. We have a viewer state for those who want to watch an
 * ongoing game, but not play.
 */
public enum ClientType {
    FIRST_PLAYER,
    SECOND_PLAYER,
    VIEWER
}
