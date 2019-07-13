package io.aktivator.resources;

import io.aktivator.model.Comment;
import io.aktivator.model.DataException;
import io.aktivator.model.UserDTO;
import io.aktivator.model.commands.CommentCreateCommand;
import io.aktivator.services.CommentService;
import io.aktivator.services.InitiativeService;
import io.aktivator.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final InitiativeService initiativeService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public CommentController(InitiativeService initiativeService, CommentService commentService, UserService userService) {
        this.initiativeService = initiativeService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentCreateCommand commentCreateCommand) {
        UserDTO user = userService.getCurrentUser();
        Comment comment = new Comment();
        try {
            comment.setInitiative(initiativeService.getInitiative(commentCreateCommand.getInitiativeId()));
            comment.setText(commentCreateCommand.getText());
            comment.setDate(commentCreateCommand.getDate());
            comment.setOwner(user.getId());
            comment = commentService.createComment(comment);

            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (DataException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        UserDTO user = userService.getCurrentUser();
        try {
            if (commentService.isOwner(commentId, user.getId())) {
                commentService.hide(commentId);
                return new ResponseEntity<>("Comment removed.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not owner of comment.", HttpStatus.UNAUTHORIZED);
            }
        } catch (DataException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}