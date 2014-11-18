package at.deder.ybr.meta;

/**
 * This annotation is used automatically generate command line usage hints for
 * a command. Only works when applied to interface ICliCommand.
 * @author lycis
 */
public @interface CliCommandInfo {
    public String command() default "";
    public String description() default "";
}
