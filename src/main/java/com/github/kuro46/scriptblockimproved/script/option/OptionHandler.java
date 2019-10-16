package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.Args;

public interface OptionHandler {

    Args getArgs();

    PreExecuteResult preExecute(final ExecutionData data);

    void execute(final ExecutionData data);
}
