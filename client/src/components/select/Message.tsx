import "../../styles/App.css";
import "../../styles/index.css";
import "../../styles/main.css";

/**
 * A interface for the props that are passed into Message.
 *
 * @params
 * message: the message to be shown
 */
interface MessageProps {
  message: string;
}
/**
 * Builds a Message component that displays the current message
 *
 * @param message: the message to be displayed
 * @returns JSX that will show the message
 */
export function Message(props: MessageProps) {
    return (
        <div className= "message-container">
            <p aria-label={props.message}>{props.message}</p>
        </div>
    );
}  





