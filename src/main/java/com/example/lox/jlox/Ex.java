package com.example.lox.jlox;

/**
 * Exit code based on UNIX
 */
public enum Ex {
    EX_OK(0, "Execution was successful"),
    EX_USAGE(64, "The command was used incorrectly"),
    EX_DATAERR(65, "The input data was incorrect"),
    EX_NOINPUT(66, "An input file did not exist or was not readable"),
    EX_NOUSER(67, "The user specified did not exist"),
    EX_NOHOST(68, "The host specified did not exist."),
    EX_UNAVAILABLE(69, "A service is unavailable"),
    EX_SOFTWARE(70, "An internal software error has been detected"),
    EX_OSERR(71, "An operating system error has been detected"),
    EX_OSFILE(72, "Some system file does not exist or cannot be opened or has some sort of error"),
    EX_CANTCREAT(73, "A user specified output file cannot be created"),
    EX_IOERR(74, "An error occurred while doing I/O on some file"),
    EX_TEMPFAIL(75, "Temporary failure, indicating something that is not really an error"),
    EX_PROTOCOL(76, "The remote system returned something that was not possible during a protocol exchange"),
    EX_NOPERM(77, "You did not have sufficient permission to perform the operation"),
    EX_CONFIG(78, "Something was found in an un-configured or mis-configured state"),
    ;

    private final int code;
    private final String message;

    Ex(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
